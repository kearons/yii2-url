package pers.yiiurl.action;

import com.intellij.ide.util.gotoByName.ContributorsBasedGotoByModel;
import com.intellij.ide.util.gotoByName.CustomMatcherModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.spring.AntPathMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GotoRequestMappingModel extends ContributorsBasedGotoByModel implements CustomMatcherModel {
    protected GotoRequestMappingModel(@NotNull Project project, @NotNull ChooseByNameContributor[] contributors) {
        super(project, contributors);
    }

    @Override
    public String getPromptText() {
        return "Enter url";
    }

    @NotNull
    @Override
    public String getNotInMessage() {
        return "No in.";
    }

    @NotNull
    @Override
    public String getNotFoundMessage() {
        return "No result.";
    }

    @Nullable
    @Override
    public String getCheckBoxName() {
        return null;
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        return false;
    }

    @Override
    public void saveInitialCheckBoxState(boolean b) {
    }

    @NotNull
    @Override
    public String[] getSeparators() {
        return new String[]{"/", "?"};
    }

    @Nullable
    @Override
    public String getFullName(@NotNull Object o) {
        return getElementName(o);
    }

    @Override
    public boolean willOpenEditor() {
        return true;
    }

    @Override
    public boolean matches(@NotNull String popupItem, @NotNull String userPattern) {
        String pattern = userPattern;
        if (pattern.equals("/")) return true;
        // REST风格的参数  @RequestMapping(value="{departmentId}/employees/{employeeId}")  PathVariable
        // REST风格的参数（正则） @RequestMapping(value="/{textualPart:[a-z-]+}.{numericPart:[\\d]+}")  PathVariable

        // pattern = StringUtils.removeRedundancyMarkup(pattern);
        //
        // userPattern 输入的过滤文字
        // DefaultChooseByNameItemProvider.buildPatternMatcher
        MinusculeMatcher matcher = NameUtil.buildMatcher("*" + pattern, NameUtil.MatchingCaseSensitivity.NONE);
        boolean matches = matcher.matches(popupItem);
        if (!matches) {
            AntPathMatcher pathMatcher = new AntPathMatcher();
            matches = pathMatcher.match(popupItem, userPattern);
        }
        return matches;
    }
}
