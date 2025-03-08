package pragmasoft.k1teauth.common.fetcher;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.views.fields.elements.Option;
import io.micronaut.views.fields.fetchers.OptionFetcher;
import io.micronaut.views.fields.messages.Message;
import jakarta.inject.Singleton;
import pragmasoft.k1teauth.user.User;

import java.util.Arrays;
import java.util.List;

@Singleton
public class UserRoleFetcher implements OptionFetcher<String> {

    @Override
    public List<Option> generate(@NonNull Class<String> type) {
        return Arrays.stream(User.Role.values())
                .map(role -> Option.builder()
                        .value(role.toString())
                        .label(Message.of(role.toString()))
                        .build()
                ).toList();
    }

    @Override
    public List<Option> generate(@NonNull String instance) {
        return Arrays.stream(User.Role.values())
                .map(role -> Option.builder()
                        .selected(role.toString().equals(instance))
                        .value(role.toString())
                        .label(Message.of(role.toString()))
                        .build()
                ).toList();
    }
}
