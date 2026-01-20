import jsPDF from 'jspdf'
import autoTable from 'jspdf-autotable'

/**
 * Export data to PDF table
 */
export function exportTableToPDF<T extends Record<string, any>>(
  data: T[],
  columns: { header: string; dataKey: string }[],
  filename: string,
  title?: string
): void {
  const doc = new jsPDF()

  // Add title if provided
  if (title) {
    doc.setFontSize(18)
    doc.text(title, 14, 22)
  }

  // Generate table
  autoTable(doc, {
    startY: title ? 30 : 20,
    head: [columns.map((col) => col.header)],
    body: data.map((row) => columns.map((col) => row[col.dataKey] || '')),
    styles: {
      fontSize: 9,
      cellPadding: 3,
    },
    headStyles: {
      fillColor: [59, 130, 246], // Primary blue
      textColor: 255,
      fontStyle: 'bold',
    },
    alternateRowStyles: {
      fillColor: [249, 250, 251], // Light gray
    },
    margin: { top: 10 },
  })

  // Add footer with date
  const pageCount = (doc as any).internal.getNumberOfPages()
  for (let i = 1; i <= pageCount; i++) {
    doc.setPage(i)
    doc.setFontSize(8)
    doc.text(
      `Generated on ${new Date().toLocaleDateString()} - Page ${i} of ${pageCount}`,
      14,
      doc.internal.pageSize.height - 10
    )
  }

  // Save the PDF
  doc.save(filename)
}

/**
 * Export medical record to PDF
 */
export function exportMedicalRecordToPDF(record: any, filename: string): void {
  const doc = new jsPDF()

  // Header
  doc.setFontSize(20)
  doc.text('VetFlow - Medical Record', 14, 22)

  // Record details
  doc.setFontSize(12)
  doc.text(`Record #${record.id}`, 14, 35)
  doc.setFontSize(10)
  doc.text(`Date: ${new Date(record.recordDate).toLocaleDateString()}`, 14, 42)
  doc.text(`Type: ${record.type}`, 14, 49)

  let yPosition = 60

  // Patient Information
  doc.setFontSize(14)
  doc.text('Patient Information', 14, yPosition)
  yPosition += 7
  doc.setFontSize(10)
  doc.text(`Name: ${record.patient?.name || 'N/A'}`, 20, yPosition)
  yPosition += 6
  doc.text(`Species: ${record.patient?.species || 'N/A'}`, 20, yPosition)
  yPosition += 6
  doc.text(`Breed: ${record.patient?.breed || 'N/A'}`, 20, yPosition)
  yPosition += 6
  doc.text(`Owner: ${record.patient?.owner?.fullName || 'N/A'}`, 20, yPosition)
  yPosition += 12

  // Veterinarian
  if (record.veterinarian) {
    doc.setFontSize(14)
    doc.text('Veterinarian', 14, yPosition)
    yPosition += 7
    doc.setFontSize(10)
    doc.text(
      `Dr. ${record.veterinarian.firstName} ${record.veterinarian.lastName}`,
      20,
      yPosition
    )
    yPosition += 12
  }

  // Chief Complaint
  if (record.chiefComplaint) {
    doc.setFontSize(14)
    doc.text('Chief Complaint', 14, yPosition)
    yPosition += 7
    doc.setFontSize(10)
    const complaintLines = doc.splitTextToSize(record.chiefComplaint, 180)
    doc.text(complaintLines, 20, yPosition)
    yPosition += complaintLines.length * 6 + 6
  }

  // Clinical Findings
  if (record.clinicalFindings) {
    doc.setFontSize(14)
    doc.text('Clinical Findings', 14, yPosition)
    yPosition += 7
    doc.setFontSize(10)
    const findingsLines = doc.splitTextToSize(record.clinicalFindings, 180)
    doc.text(findingsLines, 20, yPosition)
    yPosition += findingsLines.length * 6 + 6
  }

  // Diagnosis
  if (record.diagnosis) {
    if (yPosition > 250) {
      doc.addPage()
      yPosition = 20
    }
    doc.setFontSize(14)
    doc.text('Diagnosis', 14, yPosition)
    yPosition += 7
    doc.setFontSize(10)
    const diagnosisLines = doc.splitTextToSize(record.diagnosis, 180)
    doc.text(diagnosisLines, 20, yPosition)
    yPosition += diagnosisLines.length * 6 + 6
  }

  // Treatment
  if (record.treatment) {
    if (yPosition > 250) {
      doc.addPage()
      yPosition = 20
    }
    doc.setFontSize(14)
    doc.text('Treatment', 14, yPosition)
    yPosition += 7
    doc.setFontSize(10)
    const treatmentLines = doc.splitTextToSize(record.treatment, 180)
    doc.text(treatmentLines, 20, yPosition)
    yPosition += treatmentLines.length * 6 + 6
  }

  // Prescriptions
  if (record.prescriptions && record.prescriptions.length > 0) {
    if (yPosition > 230) {
      doc.addPage()
      yPosition = 20
    }
    doc.setFontSize(14)
    doc.text('Prescriptions', 14, yPosition)
    yPosition += 7

    autoTable(doc, {
      startY: yPosition,
      head: [['Medication', 'Dosage', 'Frequency', 'Duration']],
      body: record.prescriptions.map((p: any) => [
        p.medication,
        p.dosage,
        p.frequency,
        p.duration,
      ]),
      styles: { fontSize: 9 },
      headStyles: { fillColor: [59, 130, 246] },
    })

    yPosition = (doc as any).lastAutoTable.finalY + 10
  }

  // Footer
  doc.setFontSize(8)
  doc.text(
    `Generated on ${new Date().toLocaleDateString()}`,
    14,
    doc.internal.pageSize.height - 10
  )

  // Save
  doc.save(filename)
}
