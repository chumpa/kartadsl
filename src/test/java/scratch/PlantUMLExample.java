
package scratch;

import net.sourceforge.plantuml.SourceStringReader;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PlantUMLExample {
    public static void main(String[] args) throws Exception {
        // 1. Ваш код на PlantUML. Включаем EIP-библиотеку через URL.
        String source = "@startuml\n";
        source += "!includeurl https://raw.githubusercontent.com/plantuml-stdlib/EIP-PlantUML/main/dist/EIP-PlantUML.puml\n";
        source += "Message(newOrder, \"Новый заказ\")\n";
        source += "Splitter(splitter, \"Разделитель\")\n";
        source += "newOrder --> splitter\n";
        source += "@enduml\n";

        // 2. Создаем reader и генерируем изображение
        SourceStringReader reader = new SourceStringReader(source);
        OutputStream png = new ByteArrayOutputStream();
        // Метод outputImage сгенерирует PNG и запишет его в OutputStream
        String description = reader.outputImage(png).getDescription();

        // 3. Теперь переменная png содержит байты вашего изображения
        // (например, можно записать в файл)
        System.out.println("Изображение сгенерировано: " + description);
        // ... ваш код для сохранения png в файл
        reader.outputImage(Files.newOutputStream(Paths.get("temp/scratch01.png")));
    }
}
