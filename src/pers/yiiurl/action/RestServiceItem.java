package pers.yiiurl.action;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.pom.Navigatable;
import com.jetbrains.php.lang.psi.elements.Method;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RestServiceItem implements NavigationItem {

    private Method psiMethod;
    private String url;
    private RestServiceIcon icon;

    private Navigatable navigatableElement;

    public RestServiceItem(String url, Method psiMethod, RestServiceIcon icon) {
        this.url = url;
        this.psiMethod = psiMethod;
        this.icon = icon;

        if (psiMethod instanceof Navigatable) {
            this.navigatableElement = (Navigatable) psiMethod;
        }
    }

    @Nullable
    @Override
    public String getName() {
        return this.url;
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return new RestServiceItemPresentation();
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (null != this.navigatableElement) {
            navigatableElement.navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return this.psiMethod.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }


    private class RestServiceItemPresentation implements ItemPresentation {
        @Nullable
        @Override
        public String getPresentableText() {
            return url;
        }

        @Nullable
        @Override
        public String getLocationString() {
            return ("c".equals(icon.getIcon(1)) ? "[console]" : "[web]")
                    + "("
                    + psiMethod.getContainingClass().getName().concat("#").concat(psiMethod.getName())
                    + ")";
        }

        @Nullable
        @Override
        public Icon getIcon(boolean b) {
            return icon.getIcon();
        }
    }
}
