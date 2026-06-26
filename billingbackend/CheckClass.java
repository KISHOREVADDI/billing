import java.net.URL;
import java.net.URLClassLoader;
import java.io.File;
import java.lang.reflect.Method;

public class CheckClass {
    public static void main(String[] args) throws Exception {
        File file = new File("k:/rc/billing/billingbackend/target/classes");
        URL url = file.toURI().toURL();
        URLClassLoader loader = new URLClassLoader(new URL[]{url});
        Class<?> reqClass = loader.loadClass("com.clothshop.billing.dto.AuthRequest");
        for (Method m : reqClass.getDeclaredMethods()) {
            System.out.println(m.getName());
        }
    }
}
