package net.finmath.smartcontract.simulation;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelTableWriter implements Closeable {
	
	private final Path path;
    private final Workbook wb;

    private final CellStyle numericStyle;

    public ExcelTableWriter(Path path) throws IOException {
        this.path = path;

        if (Files.exists(path)) {
            try (InputStream in = Files.newInputStream(path)) {
                this.wb = WorkbookFactory.create(in);
            }
        } else {
            this.wb = new XSSFWorkbook();
        }

        DataFormat df = wb.createDataFormat();

        numericStyle = wb.createCellStyle();
        numericStyle.setDataFormat(df.getFormat("0.###############"));
    }
    
    
    /**
     * Writes a "column table" to a sheet.
     *
     * @param sheetName    sheet to write into
     * @param columns      LinkedHashMap preserves column order
     * @param replaceSheet if true, deletes existing sheet with same name
     * @param autosize     if true, autosize columns (can be slow on big sheets)
     */
    public void writeTable(
            String sheetName,
            LinkedHashMap<String, ? extends List<?>> columns,
            boolean replaceSheet,
            boolean autosize
    ) {
        Objects.requireNonNull(sheetName, "sheetName");
        Objects.requireNonNull(columns, "columns");

        if (columns.isEmpty()) {
            throw new IllegalArgumentException("No columns provided.");
        }

        // Determine row count (max length)
        int rowCount = columns.values().stream().mapToInt(List::size).max().orElse(0);

        Sheet sheet = getOrCreateSheet(sheetName, replaceSheet);

        // Header
        Row header = sheet.createRow(0);
        int colIdx = 0;
        for (String name : columns.keySet()) {
            Cell cell = header.createCell(colIdx++, CellType.STRING);
            cell.setCellValue(name);
        }

        // Data
        for (int r = 0; r < rowCount; r++) {
            Row row = sheet.createRow(r + 1);

            colIdx = 0;
            for (List<?> col : columns.values()) {
                Object value = (r < col.size()) ? col.get(r) : null;
                writeValue(row.createCell(colIdx++), value);
            }
        }

        if (autosize) {
            for (int c = 0; c < columns.size(); c++) {
                sheet.autoSizeColumn(c);
            }
        }
    }

    private Sheet getOrCreateSheet(String sheetName, boolean replaceSheet) {
        Sheet existing = wb.getSheet(sheetName);

        if (existing != null && replaceSheet) {
            int idx = wb.getSheetIndex(existing);
            wb.removeSheetAt(idx);
            return wb.createSheet(sheetName);
        }

        return existing != null ? existing : wb.createSheet(sheetName);
    }
    
    private void writeValue(Cell cell, Object value) {
        if (value == null) {
            cell.setBlank();
            return;
        }

        if (value instanceof Number n) {
            cell.setCellValue(n.doubleValue());
            cell.setCellStyle(numericStyle);
            return;
        }

        // Fallback: string
        cell.setCellValue(String.valueOf(value));
    }
    
    public void save() throws IOException {
        Files.createDirectories(path.toAbsolutePath().getParent());
        try (OutputStream out = Files.newOutputStream(path)) {
            wb.write(out);
        }
    }
    
	@Override
	public void close() throws IOException {
		wb.close();
	}

}
