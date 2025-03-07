package pragmasoft.k1teauth.common.fetcher;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.views.fields.elements.Option;
import io.micronaut.views.fields.fetchers.OptionFetcher;
import io.micronaut.views.fields.messages.Message;
import jakarta.inject.Singleton;
import pragmasoft.k1teauth.oauth.scope.ScopeRepository;

import java.util.List;

@Singleton
public class ScopesFetcher implements OptionFetcher<List<String>> {

    private final ScopeRepository scopeRepository;

    public ScopesFetcher(ScopeRepository scopeRepository) {
        this.scopeRepository = scopeRepository;
    }

    @Override
    public List<Option> generate(@NonNull Class<List<String>> type) {
        return scopeRepository.findAll()
                .stream()
                .map(scope -> Option.builder()
                        .value(scope.getName())
                        .label(Message.of(scope.getName()))
                        .build()
                ).toList();
    }

    @Override
    public List<Option> generate(@NonNull List<String> instance) {
        return scopeRepository.findAll()
                .stream()
                .map(scope -> Option.builder()
                        .selected(instance.contains(scope.getName()))
                        .value(scope.getName())
                        .label(Message.of(scope.getName()))
                        .build()
                ).toList();
    }
}
