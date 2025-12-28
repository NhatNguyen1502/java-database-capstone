package com.project.back_end.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "prescriptions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Prescription {

  @Id
  private String id;

  private Long appointmentId;
  private Long patientId;
  private Long doctorId;

  private String patientName;
  private String doctorName;

  private LocalDateTime prescriptionDate;
  private LocalDateTime expiryDate;

  private String status = "active";

  private List<Medication> medications;

  private String diagnosis;
  private String doctorNotes;

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  public static class Medication {
    private String name;
    private String genericName;
    private String dosage;
    private String form;
    private String frequency;
    private String duration;
    private Integer quantity;
    private String instructions;
  }
}
