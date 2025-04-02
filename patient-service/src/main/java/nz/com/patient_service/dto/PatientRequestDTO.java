package nz.com.patient_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PatientRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Email is required")
    private String dateOfBirth;

    @NotNull(message = "Registered Date is required")
    private String registeredDate;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String address;

    public @NotBlank(message = "Name is required") @Size(max = 100, message = "Name cannot exceed 100 characters") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Name is required") @Size(max = 100, message = "Name cannot exceed 100 characters") String name) {
        this.name = name;
    }

    public @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Email is required") String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(@NotBlank(message = "Email is required") String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public @NotNull(message = "Registered Date is required") String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(@NotNull(message = "Registered Date is required") String registeredDate) {
        this.registeredDate = registeredDate;
    }

    public @NotBlank(message = "Name is required") @Size(max = 255, message = "Name cannot exceed 255 characters") String getAddress() {
        return address;
    }

    public void setAddress(@NotBlank(message = "Name is required") @Size(max = 255, message = "Name cannot exceed 255 characters") String address) {
        this.address = address;
    }
}
