package tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
            "Binary: Expr left, Token operator, Expr right",
            "Grouping: Expr expression",
            "Literal: Object value",
            "Unary: Token operator, Expr right"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = String.format("%s/%s.java", outputDir, baseName);
        try (PrintWriter writer = new PrintWriter(path, "UTF-8")) {
            writer.println("package lox;");
            writer.println();
            writer.println("import java.util.List;");
            writer.println();
            writer.print(String.format("abstract class %s {", baseName));

            for (String type : types) {
                String className = type.split(":")[0].trim();
                String fields = type.split(":")[1].trim();
                writer.println();
                defineType(writer, baseName, className, fields);
            }

            writer.println("}");
            writer.println();
        }
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println(String.format("    static class %s extends %s {", className, baseName));

        String[] fields = fieldList.split(", ");

        // Fields.
        for (String field : fields) {
            writer.println(String.format("        final %s;", field));
        }

        // Contructor.
        writer.println();
        writer.println(String.format("        %s(%s) {", className, fieldList));

        // Store parameters in fields.
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println(String.format("            this.%s = %s;", name, name));
        }
        writer.println("        }");

        writer.println("    }");
    }
}
