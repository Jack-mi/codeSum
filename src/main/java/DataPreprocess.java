import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class DataPreprocess {
    public static String target_dir = "/Users/bytedance/IdeaProjects/codeSum/src/data/";
    public static String codebase_dir = "/Users/bytedance/codebase/";
    public static Integer file_idx = 0;
    public static void WriteFile(Map<String, String> methodInfo, String project_name) {
        for (Map.Entry<String, String> m : methodInfo.entrySet()) {
            String methodBody = m.getKey().strip();
            String methodComment = m.getValue();
            String label = "0 ";
            if (methodComment != null) {
                label = "1 ";
            }

            // record function
//            File file1 = new File(target_dir + "function/" + String.format("%06d", file_idx) + "func.txt");
            File file1 = new File(codebase_dir + project_name + "/allJavaFuncs/" + String.format("%06d", file_idx) + "func.txt");
            try {
                if (!file1.exists())
                    file1.createNewFile();
                BufferedWriter out = new BufferedWriter(new FileWriter(file1, false));
                out.write(methodBody);
//                out.newLine();
                out.close();
            } catch (IOException e) {
                System.out.println(e);
            }

            // record comment
//            File file2 = new File(target_dir + "comment/" + String.format("%06d", file_idx) + "comm.txt");
            File file2 = new File(codebase_dir + project_name + "/allJavaComments/" + String.format("%06d", file_idx) + "comm.txt");
            try {
                if (!file2.exists())
                    file2.createNewFile();
                BufferedWriter out2 = new BufferedWriter(new FileWriter(file2, false));
                out2.write(label);
                out2.write(methodComment==null?"":methodComment);
//                out2.newLine();
                out2.close();
            } catch (IOException e) {
                System.out.println(e);
            }
            file_idx++;
        }
    }

    public static void parser(File root, String project_name) {
        File files[] = root.listFiles();
        int cnt = 0;
        int comments = 0;
        int functions = 0;
        int total_file = files.length;
        for (File file : files) {
            Map<String, String> methodInfo = new HashMap<String, String>();
            try {
                CompilationUnit cu = StaticJavaParser.parse(file);
                // 获取函数体和注释
                MethodVisitor methodVis = new MethodVisitor();
                methodVis.visit(cu, methodInfo);
                // if current .java file contains zero function
                if (methodInfo.size() == 0)
                    continue;
//                // calculate the comments count in files
                for (Map.Entry<String, String> m : methodInfo.entrySet()) {
                    functions++;
                    if (m.getValue() != null)
                        comments++;
                }
                WriteFile(methodInfo, project_name);
            } catch(Exception e) {
                System.out.println(e);
                System.out.println(file.getName());
            }
//            cnt ++;
//            if (cnt == 10)
//                break;
        }
        System.out.println(String.format("Methods:%d", functions));
        System.out.println(String.format("Average Methods per Java Files:%f", functions*1.0/total_file*1.0));
        System.out.println(String.format("Methods with Comments:%d", comments));
        System.out.println(String.format("Comment Ratio:%f", comments*1.0/functions*1.0));
        System.out.println("------------------------------------------------------------");
    }

    private static class MethodVisitor extends VoidVisitorAdapter<Map<String, String>> {

        @Override
        public void visit(MethodDeclaration n, Map<String, String> methodInfo) {
            // 获取Method的body
            String MethodDec = n.getDeclarationAsString();
            String MethodBody = n.getBody().toString();
            MethodBody = MethodBody.substring(9, MethodBody.length()-1);
            String Method = MethodDec + ' ' + MethodBody;
            Method = Method.replace('\n', '\t');

            // 获取Method的注释
            String Comment = n.getComment().toString();
            if (!Comment.equals("Optional.empty")) {
                Comment = Comment.replaceAll("Optional", "");
                Comment = Comment.replaceAll("[/]", "");
                Comment = Comment.replaceAll("[*]", "");
                Comment = Comment.replaceAll("[ ]{2,}", " ");
                Comment = Comment.replaceAll("[\t]", " ");
                Comment = Comment.replaceAll("\n", " ");
                Comment = Comment.replace('[', ' ');
                Comment = Comment.replace(']', ' ');
                Comment = Comment.strip();
                if (Comment.length() <= 15)
                    methodInfo.put(Method, null);
                else {
                    String Comments[] = Comment.split("[.]");
                    Comment = Comments[0];
                    methodInfo.put(Method, Comment);
                }
            } else {
                methodInfo.put(Method, null);
            }
            super.visit(n, methodInfo);
        }
    }

    public static void main(String[] args) {

        File test = new File("/Users/liuxiaosu/IdeaProjects/codeSum/src/test");

        File tomcat = new File(codebase_dir + "1-tomcat" + "/allJavaFiles");
        File gradle = new File(codebase_dir + "2-gradle" + "/allJavaFiles");
        File hadoop = new File(codebase_dir + "3-hadoop" + "/allJavaFiles");
        File spring_framework = new File(codebase_dir + "4-spring-framework" + "/allJavaFiles");
        File zxing = new File(codebase_dir + "5-zxing" + "/allJavaFiles");
        File cassandra = new File(codebase_dir + "6-cassandra" + "/allJavaFiles");
        File fresco = new File(codebase_dir + "7-fresco" + "/allJavaFiles");
        File guava = new File(codebase_dir + "8-guava" + "/allJavaFiles");
        File kafka = new File(codebase_dir + "9-kafka" + "/allJavaFiles");
        File wildfly = new File(codebase_dir + "10-wildfly" + "/allJavaFiles");

//        parser(test, "test");

        System.out.println("1-tomcat");
        parser(tomcat, "1-tomcat");

        System.out.println("2-gradle");
        parser(gradle, "2-gradle");

        System.out.println("3-hadoop");
        parser(hadoop, "3-hadoop");

        System.out.println("4-spring-framework");
        parser(spring_framework, "4-spring-framework");

        System.out.println("5-zxing");
        parser(zxing, "5-zxing");

        System.out.println("6-cassandra");
        parser(cassandra, "6-cassandra");

        System.out.println("7-fresco");
        parser(fresco, "7-fresco");

        System.out.println("8-guava");
        parser(guava, "8-guava");

        System.out.println("9-kafka");
        parser(kafka, "9-kafka");

        System.out.println("10-wildfly");
        parser(wildfly, "10-wildfly");
    }
}
