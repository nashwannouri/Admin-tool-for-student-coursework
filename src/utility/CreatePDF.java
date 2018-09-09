package utility;

import java.awt.FileDialog;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.swing.JDialog;

import windows.MainWindow;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import data.Student;

/**
 * Generates a pdf that is located in the project Folder containing all the
 * students and there info
 * 
 * @author Nashwan Nouri, Darren Middleton
 *
 */
public class CreatePDF {

	private Document _document; 													// the pdf file
	private ArrayList<Student> _studentArray; 										// students the pdf will contain
	private Font headerFont = new Font(Font.FontFamily.COURIER, 14,Font.UNDERLINE); // font for headers
	private String _pdfSaveFile; 													// the position and name of the pdf file
	private Student _student; 														// student being referenced

	/**
	 * Generates a pdf file containing student information
	 * @param window  A reference to the main program window.
	 */
	public CreatePDF(MainWindow window) {
		// get all the students the pdf document will contain
		_studentArray = window.getStudents();

		// opens a save file box where pdf will be saved
		 saveFileDialog();

		// the document is being updated for every student
		try {
			// creates a pdf document
			_document = new Document();
			PdfWriter
					.getInstance(_document, new FileOutputStream(_pdfSaveFile));
			_document.open();
			// information on each student is added to the document
			for (int i = 0; i < _studentArray.size(); ++i) {
				_student = _studentArray.get(i);
				addContent();

				// starts a new page
				_document.newPage();

			}

			// document is finished
			_document.close();
		} 
		catch (Exception e) {}
	}

	/**
	 * adds all the student information to the document
	 * 
	 * @throws DocumentException
	 */
	private void addContent() throws DocumentException {

		// chapter for the individual page
		Chapter studentsFullObject = new Chapter("Student Page", 0);
		studentsFullObject.setNumberDepth(0);

		// Paragraph to which the sections will be added to
		Paragraph baseSubParagraph = new Paragraph(" ");

		// adds the students basic information to the document
		Section subSectionBasicInfo = studentsFullObject.addSection(baseSubParagraph);
		subSectionBasicInfo.setNumberDepth(0);
		getStudentInformation(subSectionBasicInfo);

		// adds the students assessment results to the document
		Section subSectionResultTable = studentsFullObject.addSection(baseSubParagraph);
		subSectionResultTable.setNumberDepth(0);
		getResultTable(subSectionResultTable);

		// adds the units visited to the document
		Section subSectionUnitVisited = studentsFullObject.addSection(baseSubParagraph);
		subSectionUnitVisited.setNumberDepth(0);
		getUnitsVisited(subSectionUnitVisited);

	}

	/**
	 * Creates a table with the students results
	 * 
	 * @param section
	 *            section where the table is added
	 * @throws DocumentException
	 *             if section could not be added to the document
	 */
	private void getResultTable(Section section) throws DocumentException {

		// Adds the title to a section.
		section.add(new Paragraph(" "));
		section.add(new Paragraph("Student Assessment Results", headerFont));
		section.add(new Paragraph(" "));

		// Creates the table with 3 rows.
		PdfPTable assessmentTable = new PdfPTable(3);
		PdfPCell firstHeader = new PdfPCell(new Phrase("Assessment Name "));
		PdfPCell secondHeader = new PdfPCell(new Phrase("Assessment Mark "));
		PdfPCell thirdHeader = new PdfPCell(new Phrase("Assessment Grade "));
		assessmentTable.addCell(firstHeader);
		assessmentTable.addCell(secondHeader);
		assessmentTable.addCell(thirdHeader);
		assessmentTable.setHorizontalAlignment(Element.ALIGN_CENTER);
		assessmentTable.setHeaderRows(1);

		// Adds the students result to the table for each assessment.
		for (int i = 0; i < _student.getStudentResultsSize(); i++) {
			assessmentTable.addCell(_student.getStudentResult(i).getAssessmentName());
			assessmentTable.addCell("" + _student.getStudentResult(i).getMark());
			assessmentTable.addCell(_student.getStudentResult(i).getGrade());
		}

		// Adds the assessment table to a section.
		section.add(assessmentTable);

		// Adds the section to the document
		section.setComplete(true);
		_document.add(section);

	}

	/**
	 * Creates a table and adds it to the section
	 * 
	 * @param section
	 *            where the table will be added
	 * @throws DocumentException
	 *             if the section could not be added to the document
	 */
	public void getUnitsVisited(Section section) throws DocumentException {

		// Adds the title to the section
		section.add(new Paragraph(" "));
		section.add(new Paragraph("Units visited", headerFont));
		section.add(new Paragraph(" "));

		// create a table with 3 headers
		PdfPTable unitsVisted = new PdfPTable(4);
		PdfPCell firstHeader = new PdfPCell(new Phrase("Module "));
		PdfPCell secondHeader = new PdfPCell(new Phrase("Town "));
		PdfPCell thirdHeader = new PdfPCell(new Phrase("Country "));
		PdfPCell forthHeader = new PdfPCell(new Phrase("Time"));
		unitsVisted.addCell(firstHeader);
		unitsVisted.addCell(secondHeader);
		unitsVisted.addCell(thirdHeader);
		unitsVisted.addCell(forthHeader);
		unitsVisted.setHorizontalAlignment(Element.ALIGN_CENTER);
		unitsVisted.setHeaderRows(1);

		// creates a table using students participation data
		for (int i = 0; i < _student.getUnitVisitedCount(); i++) {
			unitsVisted.addCell(_student.getUnitVisited(i).getModule());
			unitsVisted.addCell(_student.getUnitVisited(i).getTown());
			unitsVisted.addCell(_student.getUnitVisited(i).getCountry());
			unitsVisted.addCell(_student.getUnitVisited(i).getTime());
		}

		// Adds the table to the section
		section.add(unitsVisted);

		// add the section to the document
		section.setComplete(true);
		_document.add(section);

	}

	/**
	 * Get the students basic information
	 * 
	 * @param section
	 *            where the information will be stored
	 * @throws DocumentException
	 *             if section could not be added to the document
	 */
	private void getStudentInformation(Section section)
			throws DocumentException {

		// add the title to the Section
		section.add(new Paragraph(" "));
		section.add(new Paragraph("student information", headerFont));
		section.add(new Paragraph(" "));

		// add student basic information to section
		section.add(new Paragraph("Student Name: " + _student.getName()));
		section.add(new Paragraph("Student Number: " + _student.getNumber()));
		section.add(new Paragraph("Students Email: " + _student.getEmail()));
		section.add(new Paragraph("Tutors email: " + _student.getTutor()));

		// add the section to the document
		section.setComplete(true);
		_document.add(section);
	}

	/**
	 * Get the path of where the user wants to save the file
	 * 
	 * @return path of the saved file
	 */
	private String saveFileDialog() {
		
		// save dialog box is created
		FileDialog fileDialog = new FileDialog(new JDialog(), "Save the pdf file", FileDialog.SAVE);
		fileDialog.setDirectory("C:");
		fileDialog.setVisible(true);
		
		// Gets the file name and the path to the file
		String dir = fileDialog.getDirectory();
		String fileS = fileDialog.getFile();

		// If no file was selected return false
		if (dir == null || fileS == null)
			return _pdfSaveFile =null;

		// checks for overlap of file names
		File file = new File(dir + fileS);
		if (file.exists())
			_pdfSaveFile = dir + fileS;
		else
			_pdfSaveFile = dir + fileS + ".pdf";

		return _pdfSaveFile;
	}

}