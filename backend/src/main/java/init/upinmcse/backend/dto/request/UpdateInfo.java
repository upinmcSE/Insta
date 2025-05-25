package init.upinmcse.backend.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateInfo {
    String fullName;
    String bio;
    LocalDate dob;
    String gender;
}
