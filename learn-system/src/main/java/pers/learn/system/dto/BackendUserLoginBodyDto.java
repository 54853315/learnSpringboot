package pers.learn.system.dto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BackendUserLoginBodyDto implements Serializable {
    private static final long serialVersionUID = 1L;
    public String username;
    public String password;
    public boolean rememberMe;
}
