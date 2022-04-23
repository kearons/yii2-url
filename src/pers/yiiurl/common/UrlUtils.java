package pers.yiiurl.common;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndexImpl;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.Parameter;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import pers.yiiurl.action.RestServiceIcon;
import pers.yiiurl.action.RestServiceItem;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by oleg on 25.04.2017.
 */
public class UrlUtils {
    private static final List<String> excludeControllers =  Arrays.asList(
            "\\yii\\rest\\ActiveController",
            "\\yii\\gii\\controllers\\DefaultController",
            "\\yii\\rest\\Controller",
            "\\yii\\debug\\controllers\\DefaultController",
            "\\yii\\debug\\controllers\\UserController"
    );

    private static Collection<PhpClass> getClassesByParent(String parentFqn, @NotNull Project project) {
        Collection<PhpClass> subclasses = new ArrayList<>();
        Collection<PhpClass> directSubclasses = PhpIndexImpl.getInstance(project).getDirectSubclasses(parentFqn);
        for (PhpClass directSubclass: directSubclasses) {
            subclasses.addAll(getClassesByParent(directSubclass.getFQN(), project));
        }
        subclasses.addAll(directSubclasses);
        return subclasses;
    }

    private static Collection<PhpClass> getControllers(Project project) {
        return getClassesByParent("\\yii\\web\\Controller", project);
    }
    private static Collection<PhpClass> getConsoleControllers(Project project) {
        return getClassesByParent("\\yii\\console\\Controller", project);
    }

    public static List<RestServiceItem> getRoutes(Project project) {
        Collection<PhpClass> webControllers = getControllers(project);
        List<RestServiceItem> list = new ArrayList<>();
        for (PhpClass controller: webControllers) {
            if (!excludeControllers.contains(controller.getFQN()))
                list.addAll(controllerToRoutes(controller, RestServiceIcon.Web));
        }
        Collection<PhpClass> consoleControllers = getConsoleControllers(project);
        for (PhpClass controller: consoleControllers) {
            if (!excludeControllers.contains(controller.getFQN()))
                list.addAll(controllerToRoutes(controller, RestServiceIcon.Console));
        }
        return list;
    }

    private static List<RestServiceItem> controllerToRoutes(PhpClass controller, RestServiceIcon icon) {
        String controllerName = controller.getName();
        String controllerPart = controllerName.substring(0, controllerName.length() - 10);
        controllerPart = getPrefix(controller) + "/" + StringUtils.CamelToId(controllerPart, "-");

        Collection<Method> methods = controller.getMethods();
        List<RestServiceItem> routes = new ArrayList<>();
        for (Method method: methods) {
            String methodName = method.getName();
            if (methodName.length() > 6 && methodName.startsWith("action") && Character.isUpperCase(methodName.charAt(6))) {
                String actionPart = methodName.substring(6); // remove "action" prefix
                actionPart = StringUtils.CamelToId(actionPart, "-"); // part2.replaceAll("(?<=[\\p{Lower}\\p{Digit}])[\\p{Upper}]", "-$0").toLowerCase();

                RestServiceItem item = new RestServiceItem(controllerPart + "/" +actionPart, method, icon);
                routes.add(item);
            }
        }

        return routes;
    }

    private static String getPrefix(PhpClass controller) {
        String[] names = controller.getNamespaceName().split("\\\\");
        boolean flag = false;
        String prefix = "";
        for (String name: names) {
            if (flag) {
                prefix += "/" + name.toLowerCase();
            }
            if (!flag && "controllers".equals(name)) flag = true;
        }
        return prefix;
    }

    public static HashMap<String, Method> getRoutes1(Project project) {
        Collection<PhpClass> controllers = getControllers(project);
        HashMap<String, Method> routes = new HashMap<>();
        for (PhpClass controller: controllers) {
            if (!excludeControllers.contains(controller.getFQN()))
                routes.putAll(controllerToRoutes1(controller));
        }

        return routes;
    }

    private static HashMap<String, Method> controllerToRoutes1(PhpClass controller) {
        String controllerName = controller.getName();
        String controllerPart = controllerName.substring(0, controllerName.length() - 10);
        controllerPart = StringUtils.CamelToId(controllerPart, "-");

        Collection<Method> methods = controller.getMethods();
        HashMap<String, Method> routes = new HashMap<>();
        for (Method method: methods ) {
            String methodName = method.getName();
            if (methodName.length() > 6 && methodName.startsWith("action") && Character.isUpperCase(methodName.charAt(6))) {
                String actionPart = methodName.substring(6); // remove "action" prefix
                actionPart = StringUtils.CamelToId(actionPart, "-"); // part2.replaceAll("(?<=[\\p{Lower}\\p{Digit}])[\\p{Upper}]", "-$0").toLowerCase();

                routes.put(controllerPart + "/" +actionPart, method);
            }
        }

        return routes;
    }

    public static Parameter[] getParamsByUrl(String url, Project project) {
        final HashMap<String, Method> routes = getRoutes1(project);
        if (routes.containsKey(url)) {
            Method method = routes.get(url);
            return method.getParameters();
        }

        return null;
    }
}
