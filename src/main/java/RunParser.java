import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

public class RunParser {
    public static void main(String[] args) throws IOException {
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

    private static void getFileStructure(File file) throws IOException {
        if (file.isFile()) {
            if (file.getName().endsWith(".json")) {
                String json = ReadFromFile.readToString(file.getPath());
                //System.out.println(asYaml(json));
                // записіваем в файл.yaml в папку конвертед
            }
            if (file.getName().endsWith(".yaml")) {
                String yaml = ReadFromFile.readToString(file.getPath());
                System.out.println(asJson(yaml));
                writeToFile(file.getName(), asJson(yaml));
                // записываем файл.json в папку конвертед
            }
            //System.out.println(file.getName());
        } else {
            //System.out.println(file.getName());
            for (File f : Objects.requireNonNull(file.listFiles())) {
                getFileStructure(f);
            }
        }
    }

    private static String asYaml(String jsonString) throws JsonProcessingException {
        JsonNode jsonNodeTree = new ObjectMapper().readTree(jsonString);
        String res = new ObjectMapper().writeValueAsString(jsonNodeTree);
        return res;
    }

    private static String asJson(String yaml) {
        Yaml yaml1 = new Yaml();
        Map<String, Object> map = yaml1.load(yaml);
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }

    private static void writeToFile(String fileName, String info) throws IOException {
        Path pathDir = FileSystems.getDefault().getPath("").toAbsolutePath();
        System.out.println(pathDir);
        String dirName = "converted";
        File dir = new File(String.valueOf(pathDir));
        File file = new File(dir+File.separator.concat(dirName), fileName);
        file.createNewFile();
        Files.write(Path.of(file.getPath()),info.getBytes());
    }

}
