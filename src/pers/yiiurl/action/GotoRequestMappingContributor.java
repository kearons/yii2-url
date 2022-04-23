package pers.yiiurl.action;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import pers.yiiurl.common.UrlUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GotoRequestMappingContributor implements ChooseByNameContributor {

    private List<RestServiceItem> navItem;

    public GotoRequestMappingContributor() {}

    @NotNull
    @Override
    public String[] getNames(Project project, boolean b) {
        String[] names = null;
        List<RestServiceItem> routeList = UrlUtils.getRoutes(project);

        navItem = routeList;

        if (routeList != null) names = new String[routeList.size()];

        for (int i = 0; i < routeList.size(); i++) {
            RestServiceItem requestMappingNavigationItem = routeList.get(i);
            names[i] = requestMappingNavigationItem.getName();
        }

        return names;
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean onlyThisModuleChecked) {
        NavigationItem[] navigationItems = navItem.stream().filter(item -> item.getName().equals(name)).toArray(NavigationItem[]::new);
        return navigationItems;
    }
}
