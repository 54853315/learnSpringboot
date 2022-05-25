package pers.learn.web.request;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class BackendUserLoginBodyDto {
    public String username;
    public String password;
}
