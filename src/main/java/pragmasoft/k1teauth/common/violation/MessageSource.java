package pragmasoft.k1teauth.common.violation;

import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Path.Node;

import java.util.List;
import java.util.Set;

@Singleton
public class MessageSource {

    public List<String> violationsMessages(Set<ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(MessageSource::violationMessage)
                .toList();
    }

    private static String violationMessage(ConstraintViolation<?> violation) {
        StringBuilder sb = new StringBuilder();
        Node lastNode = lastNode(violation.getPropertyPath());
        if (lastNode != null) {
            sb.append(lastNode.getName());
            sb.append(" ");
        }
        sb.append(violation.getMessage());
        return sb.toString();
    }

    private static Node lastNode(Path path) {
        Node lastNode = null;
        for (Node node : path) {
            lastNode = node;
        }
        return lastNode;
    }
}
