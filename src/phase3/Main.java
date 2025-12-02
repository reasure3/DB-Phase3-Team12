package phase3;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = resolvePort(args);
        File docBase = prepareDocBase();

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir(createTempDir("tomcat-work").toString());

        Context context = tomcat.addContext("", docBase.getAbsolutePath());

        // 서블릿 클래스는 어노테이션(@WebServlet) 기반으로 로딩되거나 web.xml에 정의되어야 합니다.
        // 각 기능별 서블릿을 추가할 때 아래와 같은 방식으로 컨테이너에 등록할 수 있습니다.
        // Tomcat.addServlet(context, "ExampleServlet", new ExampleServlet());
        // context.addServletMappingDecoded("/example", "ExampleServlet");

        System.out.println("Starting embedded Tomcat on port " + port);
        tomcat.start();
        tomcat.getServer().await();
    }

    private static int resolvePort(String[] args) {
        if (args != null && args.length > 0) {
            try {
                return Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                // fallback to environment or default
            }
        }

        String portEnv = System.getenv("PORT");
        if (portEnv != null) {
            try {
                return Integer.parseInt(portEnv);
            } catch (NumberFormatException ignored) {
                // ignore and fallback to default
            }
        }
        return 8080;
    }

    private static File prepareDocBase() throws Exception {
        Path webappPath = Paths.get("src", "main", "webapp");
        if (Files.exists(webappPath)) {
            return webappPath.toFile();
        }

        // 웹 리소스가 아직 없다면 Tomcat이 요구하는 문서 루트를 임시로 생성한다.
        Path tempDir = createTempDir("tomcat-docbase");
        Files.createDirectories(tempDir);
        return tempDir.toFile();
    }

    private static Path createTempDir(String prefix) throws Exception {
        return Files.createTempDirectory(prefix);
    }
}
