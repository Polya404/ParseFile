import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class RunParser {
    static long resAsJson;
    static long resAsYaml;

    public static void main(String[] args) throws IOException, InterruptedException {
        File file;
        if (args.length == 0) {
            Path pathDir = FileSystems.getDefault().getPath("").toAbsolutePath();
            String s = pathDir.toAbsolutePath().toString();
            file = new File(s);
        } else {
            file = new File(args[0]);
        }

        getFileStructure(file);
    }

    private static void getFileStructure(File file) throws IOException, InterruptedException {
        if (file.isFile()) {
            if (file.getName().endsWith(".json")) {
                String json = ReadFromFile.readToString(file.getPath());
                //System.out.println(asYaml(json));
                writeToFile(file.getName(), asYaml(json));
                writeLog(file);
            }
            if (file.getName().endsWith(".yaml")) {
                String yaml = ReadFromFile.readToString(file.getPath());
                //System.out.println(asJson(yaml));
                writeToFile(file.getName(), asJson(yaml));
                writeLog(file);
            }
        } else {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                getFileStructure(f);
            }
        }
    }

    private static String asYaml(String jsonString) throws JsonProcessingException, InterruptedException {
        long start = System.currentTimeMillis();
        String res = "";
        try {
            Gson gson = new Gson();
            Map[] map = gson.fromJson(jsonString, Map[].class);
            String str = gson.toJson(map);
            //System.out.println(str);

            Yaml yaml = new Yaml();

            JSONObject jsonobject = new JSONObject(map);
            String prettyJSONString = jsonobject.toString(4);
            Map<String, Object> map1 = yaml.load(prettyJSONString);
            res = yaml.dump(map);

            //System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
            return "File can not be converted";
        }
        Thread.sleep(1000);
        long finish = System.currentTimeMillis();
        resAsYaml = finish - start;
        return res;
    }

    private static String asJson(String yaml) throws InterruptedException {
        long start = System.currentTimeMillis();
        Yaml yaml1 = new Yaml();
        StringBuilder jsonObject = new StringBuilder();
        try {
            ArrayList list = yaml1.load(yaml);
            for (int i = 0; i < list.size(); i++) {
                String str = new Gson().toJson(list.get(i), Map.class);
                Map<String, Object> map = yaml1.load(str);
                if (i == list.size() - 1) {
                    jsonObject.append(new JSONObject(map));
                } else {
                    jsonObject.append(new JSONObject(map)).append(",");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "File can not be converted";
        }

        Thread.sleep(1000);
        long finish = System.currentTimeMillis();
        resAsJson = finish - start;
        return "[" + jsonObject + "]";
    }

    private static void writeToFile(String fileName, String info) throws IOException {
        Path pathDir = FileSystems.getDefault().getPath("").toAbsolutePath();
        String dirName = "converted";
        File dir = new File(String.valueOf(pathDir));
        if (fileName.endsWith(".yaml")) {
            fileName = fileName.replaceAll(".yaml", ".json");
        } else if (fileName.endsWith(".json")) {
            fileName = fileName.replaceAll(".json", ".yaml");
        }
        File file = new File(dir + File.separator.concat(dirName), fileName);
        file.createNewFile();
        Files.write(Path.of(file.getPath()), info.getBytes());
    }

    private static void writeLog(File oldFile) throws IOException {
        String info = "";
        String newFileName = "";
        Path pathDir = FileSystems.getDefault().getPath("").toAbsolutePath();
        if (oldFile.getName().endsWith(".yaml")) {
            newFileName = oldFile.getName().replaceAll(".yaml", ".json");
        }
        if (oldFile.getName().endsWith(".json")) {
            newFileName = oldFile.getName().replaceAll(".json", ".yaml");
        }
        Path currentDir = Path.of(pathDir + File.separator.concat("converted") + File.separator.concat(newFileName));
        System.out.println(currentDir);
        File newFile = new File(String.valueOf(currentDir));

        File file = new File(pathDir + File.separator.concat("result.log"));
        file.createNewFile();
        if (oldFile.getName().endsWith(".json")) {
            info = oldFile.getName() + " -> " + newFileName + "; " + resAsYaml + "ms; " + oldFile.length() + " -> " + newFile.length() + "\n";
        }
        if (oldFile.getName().endsWith(".yaml")) {
            info = oldFile.getName() + " -> " + newFileName + "; " + resAsJson + "ms; " + oldFile.length() + " -> " + newFile.length() + "\n";
        }
        Files.write(Path.of(file.getPath()), info.getBytes(), StandardOpenOption.APPEND);
    }

}
