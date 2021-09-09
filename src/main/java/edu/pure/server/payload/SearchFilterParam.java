package edu.pure.server.payload;

import edu.pure.server.opedukg.entity.KnowledgeBaseEntity;
import edu.pure.server.opedukg.entity.SearchResult;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class SearchFilterParam {
    public static final SearchFilterParam DEFAULT = new SearchFilterParam();

    @Getter
    @Setter
    private String pattern = "";
    private Boolean isRegularExpression = false;
    @Setter
    private Boolean exclusive = false;

    public void setAsRegularExpression(final Boolean isRegularExpression) {
        this.isRegularExpression = isRegularExpression;
    }

    public Predicate<SearchResult> asPredicate() {
        Predicate<String> filter;
        if (this.isRegularExpression()) { // 使用正则时，感觉比较符合直觉的是用精确匹配
            filter = Pattern.compile(this.getPattern()).asMatchPredicate();
        } else { // 不用正则的时候，子串匹配就好
            filter = Pattern.compile(Pattern.quote(this.getPattern())).asPredicate();
        }
        if (this.isExclusive()) {
            filter = Predicate.not(filter);
        }
        return Function.<SearchResult>identity()
                       .andThen(SearchResult::getEntity)
                       .andThen(KnowledgeBaseEntity::getName)
                       .andThen(filter::test)
                ::apply;
    }

    public Boolean isRegularExpression() {return this.isRegularExpression;}

    public Boolean isExclusive() {return this.exclusive;}
}
