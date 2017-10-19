package cc.gospy.ext;

import cc.gospy.core.entity.Result;
import cc.gospy.core.pipeline.PipeException;
import cc.gospy.core.pipeline.Pipeline;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelPipeline implements Pipeline {
    private String path;

    private ExcelPipeline(String path) {
        this.path = path;
    }

    public static ExcelPipeline getDefault() {
        return new Builder().build();
    }

    public static Builder custom() {
        return new Builder();
    }

    @Override
    public void pipe(Result<?> result) throws PipeException {
        Object data = result.getData();
        if (data == null) {
            return;
        }
        if (data.getClass() != List[].class) {
            throw new RuntimeException("cannot match type: " + data.getClass());
        }
        File file = new File(path);
        if (!file.exists()) {
            firstWrite(path, (List[]) data);
        } else {
            appendWrite(path, (List[]) data);
        }
    }

    public void firstWrite(String filePath, List[] data) throws PipeException {
        String[] title = {"#", "url", "xpath", "data"};
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < title.length; i++) {
            row.createCell(i).setCellValue(title[i]);
        }
        List<String> urls = data[0];
        List<String> xpaths = data[1];
        List<String> datas = data[2];
        int maxRowCount = Math.max(urls.size(), Math.max(xpaths.size(), datas.size()));
        for (int i = 1; i <= maxRowCount; i++) {
            HSSFRow newRow = sheet.createRow(i);
            newRow.createCell(0).setCellValue(i);
            newRow.createCell(1).setCellValue(i <= urls.size() ? urls.get(i - 1) : "");
            newRow.createCell(2).setCellValue(i <= xpaths.size() ? xpaths.get(i - 1) : "");
            newRow.createCell(3).setCellValue(i <= datas.size() ? datas.get(i - 1) : "");
        }
        File file = new File(filePath);
        try {
            file.createNewFile();
            try (FileOutputStream stream = FileUtils.openOutputStream(file)) {
                workbook.write(stream);
            }
        } catch (IOException e) {
            throw new PipeException(e.getMessage());
        }
    }

    public void appendWrite(String filePath, List[] data) throws PipeException {
        try {
            FileInputStream inputStream = new FileInputStream(filePath);
            POIFSFileSystem poifsFileSystem = new POIFSFileSystem(inputStream);
            HSSFWorkbook workbook = new HSSFWorkbook(poifsFileSystem);
            HSSFSheet sheet = workbook.getSheetAt(0);
            int rowStart = sheet.getLastRowNum();
            List<String> urls = data[0];
            List<String> xpaths = data[1];
            List<String> datas = data[2];
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                int maxRowCount = Math.max(urls.size(), Math.max(xpaths.size(), datas.size()));
                for (int i = 1; i <= maxRowCount; i++) {
                    HSSFRow newRow = sheet.createRow(rowStart + i);
                    newRow.createCell(0).setCellValue(rowStart + i);
                    newRow.createCell(1).setCellValue(i <= urls.size() ? urls.get(i - 1) : "");
                    newRow.createCell(2).setCellValue(i <= xpaths.size() ? xpaths.get(i - 1) : "");
                    newRow.createCell(3).setCellValue(i <= datas.size() ? datas.get(i - 1) : "");
                }
                outputStream.flush();
                workbook.write(outputStream);
            }
        } catch (Exception e) {
            throw new PipeException(e.getMessage());
        }
    }

    @Override
    public Class getAcceptedDataType() {
        return List[].class;
    }

    public static class Builder {
        private String path;

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public ExcelPipeline build() {
            return new ExcelPipeline(path);
        }
    }

}
