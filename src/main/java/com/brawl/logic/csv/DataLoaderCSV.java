package com.brawl.logic.csv;

import com.brawl.logic.data.LogicData;
import com.brawl.logic.utils.LogicReflectionUtils;
import de.siegmar.fastcsv.reader.CommentStrategy;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class DataLoaderCSV {

    public static LogicData[] load(Class<?> claz, int classId, String path) throws Exception {
        ArrayList<String> header = new ArrayList<>(), types = new ArrayList<>();
        HashMap<Integer, LogicData> instances = new HashMap<>();
        int idx = 0, id = 0;
        try (CsvReader rows = CsvReader.builder().fieldSeparator(',').quoteCharacter('"')
                .commentStrategy(CommentStrategy.SKIP).commentCharacter('#').errorOnDifferentFieldCount(false)
                .build(Paths.get(path))) {
            for (CsvRow row : rows) {
                if (idx == 0 || idx == 1) {
                    ArrayList<String> list = idx == 0 ? header : types;
                    list.addAll(row.getFields());
                    idx++;
                    continue;
                }
                idx++;

                if (row.getField(0).trim().isEmpty())
                    continue;

                Object object = claz.getDeclaredConstructor().newInstance();

                for (int i = 0; i < row.getFieldCount(); i++)
                    LogicReflectionUtils.set(object, header.get(i), row.getField(i), types.get(i));

                LogicData data = ((LogicData) object);
                data.setDataId(id);
                data.setClassId(classId);
                instances.put(id, data);
                id++;
            }
        }
        LogicData[] data = new LogicData[instances.size()];
        for (int i = 0; i < instances.size(); i++) {
            data[i] = instances.get(i);
        }
        return data;
    }

}
