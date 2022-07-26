import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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
                //writeToFile(file.getName(), asYaml(json));
            }
            if (file.getName().endsWith(".yaml")) {
                String yaml = ReadFromFile.readToString(file.getPath());
                System.out.println(asJson(yaml));
                writeToFile(file.getName(), asJson(yaml));
                writeLog(file);
            }
            //System.out.println(file.getName());
        } else {
            //System.out.println(file.getName());
            for (File f : Objects.requireNonNull(file.listFiles())) {
                getFileStructure(f);
            }
        }
    }

    private static String asYaml(String jsonString) throws JsonProcessingException, InterruptedException {
        long start = System.currentTimeMillis();
        JsonNode jsonNodeTree = new ObjectMapper().readTree(jsonString);
        String res = new ObjectMapper().writeValueAsString(jsonNodeTree);
        Thread.sleep(1000);
        long finish = System.currentTimeMillis();
        resAsYaml = finish - start;
        return res;
    }

    private static String asJson(String yaml) throws InterruptedException {
        long start = System.currentTimeMillis();
        Yaml yaml1 = new Yaml();
        Map<String, Object> map = yaml1.load(yaml);
        JSONObject jsonObject = new JSONObject(map);
        Thread.sleep(1000);
        long finish = System.currentTimeMillis();
        resAsJson = finish - start;
        return jsonObject.toString();
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
            info = oldFile.getName() + " -> " + newFileName + "; " + resAsYaml + "ms; " + oldFile.length() + " -> " + newFile.length();
        }
        if (oldFile.getName().endsWith(".yaml")) {
            info = oldFile.getName() + " -> " + newFileName + "; " + resAsJson + "ms; " + oldFile.length() + " -> " + newFile.length();
        }
        Files.write(Path.of(file.getPath()), info.getBytes());
    }

}
