package com.codealpha.cv;

// ---- iText 7 (PDF) ----
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TabAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

// ---- Apache POI (DOCX) ----
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabStop;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Generates a professional CV (resume) as BOTH a PDF and a DOCX file, styled after
 * a classic single-column resume: centered name/contact header, section titles with
 * an underline rule, dated entries, and bullet points.
 *
 * <p>Run in IntelliJ IDEA: open this Maven project, then either right-click this file
 * and choose "Run 'CVGenerator.main()'", or run {@code mvn compile exec:java}.
 * It writes {@code Edward_Abasi_Edgar_CV.pdf} and {@code Edward_Abasi_Edgar_CV.docx}
 * into the project directory.</p>
 */
public class CVGenerator {

    // =====================================================================================
    // Simple data model (kept in-file so the whole app is one class)
    // =====================================================================================

    /** A dated entry: a title with an optional date, an italic subtitle, and bullet points. */
    static class Entry {
        final String title;
        final String date;
        final String subtitle;
        final java.util.List<String> bullets;

        Entry(String title, String date, String subtitle, String... bullets) {
            this.title = title;
            this.date = date;
            this.subtitle = subtitle;
            this.bullets = new ArrayList<>(Arrays.asList(bullets));
        }
    }

    // --- Header ---
    static final String NAME = "EDWARD ABASI EDGAR";
    static final String CONTACT = "Mbeya, Tanzania  \u2022  2557657897  \u2022  edgar@must.ac.tz";

    // --- Summary ---
    static final String SUMMARY =
            "Project Manager. Results-driven manager with 5+ years of experience leading "
            + "cross-functional tech teams, having delivered 10+ projects with a 96% on-time "
            + "record and reduced delays by 30% through improved scheduling.";

    // --- Simple bulleted sections ---
    static final java.util.List<String> SKILL_HIGHLIGHTS = Arrays.asList(
            "Communication skills",
            "Computer skills",
            "Team Leadership",
            "Project and research");

    static final java.util.List<String> AWARDS = Arrays.asList("BA Statistics");
    static final java.util.List<String> MEMBERSHIP = Arrays.asList("GDSC Must");

    // --- Work experience ---
    static final java.util.List<Entry> WORK_EXPERIENCE = Arrays.asList(
            new Entry("New York-Presbyterian Hospital", "June 2016 \u2013 August 2016",
                    "Operations Intern, Westchester Division & Weill Cornell Campus",
                    "Identified opportunities to improve patient flow from the psychiatric emergency "
                            + "department to the inpatient unit using Lean methodology; created process maps "
                            + "and analyzed patient wait time data.",
                    "Developed strategy to reduce the median boarding time of patients in the psychiatric "
                            + "emergency department and increase the percentage of patients transferred to "
                            + "inpatient units before noon from 2.5% to 25%; presented the strategy to the "
                            + "hospital's Executive Leadership Team.",
                    "Analyzed patient readmission trends and developed a dashboard for monthly reporting."),
            new Entry("Research Assistant/Programmer", "August 2013 \u2013 June 2015",
                    "Mathematica Policy Research",
                    "Designed quantitative analyses of electronic health record data to evaluate the "
                            + "reliability and validity of two clinical quality measures of cancer care; "
                            + "managed a team of three programmers.",
                    "Identified best practices for ACA State-Based Marketplace (SBM) eligibility and "
                            + "enrollment business processes through qualitative analysis of interviews with "
                            + "SBM officials; drafted sections of final report.",
                    "Developed structured interview protocols and led interviews with patient stakeholders "
                            + "to assess the usability of a survey instrument for behavioral health services.",
                    "Conducted detailed analysis (using SAS and Stata) on student and teacher data for an "
                            + "evaluation of the Teacher Incentive Fund.",
                    "Managed finances, created internal and external progress reports, and drafted sections "
                            + "of business proposals, as Project Manager for two federal contracts."));

    // --- Education ---
    static final java.util.List<Entry> EDUCATION = Arrays.asList(
            new Entry("Master of Public Health Candidate in Health Management", "Expected May 2017",
                    "Harvard T.H. Chan School of Public Health"));

    // --- Research project ---
    static final java.util.List<Entry> RESEARCH_PROJECT = Arrays.asList(
            new Entry("Public Affairs Intern, Office of the U.S. Global AIDS Coordinator",
                    "June 2012 \u2013 August 2012", null,
                    "Synthesized news stories and research articles into talking points, blog posts, slide "
                            + "presentations, and briefing memos for U.S. Global AIDS Coordinator Eric Goosby.",
                    "Wrote and edited internal and external communications for the President's Emergency Plan "
                            + "For AIDS Relief (PEPFAR) including a weekly newsletter and daily news briefs."));

    // --- Professional training ---
    static final java.util.List<Entry> PROFESSIONAL_TRAINING = Arrays.asList(
            new Entry("Research Assistant/Programmer", "June 2021", "Mathematica Policy Research",
                    "Designed quantitative analyses of electronic health record data to evaluate the "
                            + "reliability and validity of two clinical quality measures of cancer care; "
                            + "managed a team of three programmers.",
                    "Identified best practices for ACA State-Based Marketplace (SBM) eligibility and "
                            + "enrollment business processes through qualitative analysis of interviews with "
                            + "SBM officials; drafted sections of final report."));

    // --- References ---
    static final java.util.List<Entry> REFERENCES = Arrays.asList(
            new Entry("John Kjazi", "CEO at Nexusnet", "Email: johnkjazi@nexusnet.xom | Phone: 255678654"),
            new Entry("Temba Zwane", "Trainer at NIT", "Email: temba@nit.ac.tz | Phone: 255745687"));

    static final String DECLARATION =
            "I EDWARD ABASI EDGAR, declare that the information given in this CV is true.";

    // =====================================================================================
    // Entry point
    // =====================================================================================

    public static void main(String[] args) throws Exception {
        String pdfPath = "Edward_Abasi_Edgar_CV.pdf";
        String docxPath = "Edward_Abasi_Edgar_CV.docx";
        generatePdf(pdfPath);
        generateDocx(docxPath);
        System.out.println("Generated:");
        System.out.println("  " + new java.io.File(pdfPath).getAbsolutePath());
        System.out.println("  " + new java.io.File(docxPath).getAbsolutePath());
    }

    // =====================================================================================
    // PDF generation (iText 7)
    // =====================================================================================

    private static void generatePdf(String path) throws Exception {
        PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

        try (PdfDocument pdf = new PdfDocument(new PdfWriter(path));
             Document doc = new Document(pdf, PageSize.A4)) {

            doc.setMargins(36, 45, 36, 45);
            doc.setFont(regular).setFontSize(9.5f);

            // Header
            doc.add(new Paragraph(NAME).setFont(bold).setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(2));
            doc.add(new Paragraph(CONTACT).setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginBottom(6));

            // Summary
            pdfSectionHeader(doc, bold, "SUMMARY");
            doc.add(new Paragraph(SUMMARY).setMarginBottom(4));

            // Skill highlights
            pdfSectionHeader(doc, bold, "SKILL HIGHLIGHTS");
            pdfBullets(doc, SKILL_HIGHLIGHTS);

            // Work experience
            pdfSectionHeader(doc, bold, "WORK EXPERIENCE");
            for (Entry e : WORK_EXPERIENCE) pdfEntry(doc, bold, italic, e);

            // Education
            pdfSectionHeader(doc, bold, "EDUCATION");
            for (Entry e : EDUCATION) pdfEntry(doc, bold, italic, e);

            // Research project
            pdfSectionHeader(doc, bold, "RESEARCH PROJECT");
            for (Entry e : RESEARCH_PROJECT) pdfEntry(doc, bold, italic, e);

            // Professional training
            pdfSectionHeader(doc, bold, "PROFESSIONAL TRAINING");
            for (Entry e : PROFESSIONAL_TRAINING) pdfEntry(doc, bold, italic, e);

            // Awards and certifications
            pdfSectionHeader(doc, bold, "AWARDS AND CERTIFICATIONS");
            pdfBullets(doc, AWARDS);

            // Membership and affiliation
            pdfSectionHeader(doc, bold, "MEMBERSHIP AND AFFILIATION");
            pdfBullets(doc, MEMBERSHIP);

            // References
            pdfSectionHeader(doc, bold, "REFERENCES");
            for (Entry r : REFERENCES) {
                Paragraph p = new Paragraph().setMarginBottom(4);
                p.add(new Text(r.title).setFont(bold));
                if (r.date != null) p.add(new Text("  \u2014  " + r.date));
                doc.add(p);
                if (r.subtitle != null) doc.add(new Paragraph(r.subtitle).setMarginTop(0));
            }

            // Declaration
            pdfSectionHeader(doc, bold, "DECLARATION");
            doc.add(new Paragraph(DECLARATION));
        }
    }

    private static void pdfSectionHeader(Document doc, PdfFont bold, String title) {
        Paragraph p = new Paragraph(title).setFont(bold).setFontSize(11)
                .setMarginTop(10).setMarginBottom(4).setPaddingBottom(2)
                .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.75f));
        p.setWidth(UnitValue.createPercentValue(100));
        doc.add(p);
    }

    private static void pdfBullets(Document doc, java.util.List<String> items) {
        List list = new List().setSymbolIndent(6).setListSymbol("\u2022 ")
                .setMarginLeft(6).setMarginBottom(4);
        for (String item : items) list.add(new ListItem(item));
        doc.add(list);
    }

    private static void pdfEntry(Document doc, PdfFont bold, PdfFont italic, Entry e) {
        Paragraph header = new Paragraph().setMarginBottom(0).setMarginTop(2)
                .addTabStops(new TabStop(515, TabAlignment.RIGHT));
        header.add(new Text(e.title).setFont(bold));
        if (e.date != null) {
            header.add(new Tab());
            header.add(new Text(e.date).setFontColor(ColorConstants.DARK_GRAY));
        }
        doc.add(header);
        if (e.subtitle != null) {
            doc.add(new Paragraph(e.subtitle).setFont(italic).setFontColor(ColorConstants.DARK_GRAY)
                    .setMarginTop(0).setMarginBottom(2));
        }
        if (!e.bullets.isEmpty()) pdfBullets(doc, e.bullets);
    }

    // =====================================================================================
    // DOCX generation (Apache POI)
    // =====================================================================================

    private static void generateDocx(String path) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(); OutputStream out = new FileOutputStream(path)) {

            // Header: name
            XWPFParagraph name = doc.createParagraph();
            name.setAlignment(ParagraphAlignment.CENTER);
            name.setSpacingAfter(20);
            XWPFRun nameRun = name.createRun();
            nameRun.setText(NAME);
            nameRun.setBold(true);
            nameRun.setFontSize(20);

            // Header: contact
            XWPFParagraph contact = doc.createParagraph();
            contact.setAlignment(ParagraphAlignment.CENTER);
            contact.setSpacingAfter(120);
            XWPFRun contactRun = contact.createRun();
            contactRun.setText(CONTACT);
            contactRun.setFontSize(9);
            contactRun.setColor("444444");

            docxSectionHeader(doc, "SUMMARY");
            docxParagraph(doc, SUMMARY);

            docxSectionHeader(doc, "SKILL HIGHLIGHTS");
            docxBullets(doc, SKILL_HIGHLIGHTS);

            docxSectionHeader(doc, "WORK EXPERIENCE");
            for (Entry e : WORK_EXPERIENCE) docxEntry(doc, e);

            docxSectionHeader(doc, "EDUCATION");
            for (Entry e : EDUCATION) docxEntry(doc, e);

            docxSectionHeader(doc, "RESEARCH PROJECT");
            for (Entry e : RESEARCH_PROJECT) docxEntry(doc, e);

            docxSectionHeader(doc, "PROFESSIONAL TRAINING");
            for (Entry e : PROFESSIONAL_TRAINING) docxEntry(doc, e);

            docxSectionHeader(doc, "AWARDS AND CERTIFICATIONS");
            docxBullets(doc, AWARDS);

            docxSectionHeader(doc, "MEMBERSHIP AND AFFILIATION");
            docxBullets(doc, MEMBERSHIP);

            docxSectionHeader(doc, "REFERENCES");
            for (Entry r : REFERENCES) {
                XWPFParagraph p = doc.createParagraph();
                p.setSpacingAfter(40);
                XWPFRun t = p.createRun();
                t.setBold(true);
                t.setText(r.title);
                if (r.date != null) {
                    XWPFRun d = p.createRun();
                    d.setText("  \u2014  " + r.date);
                }
                if (r.subtitle != null) {
                    p.createRun().addBreak();
                    XWPFRun s = p.createRun();
                    s.setText(r.subtitle);
                }
            }

            docxSectionHeader(doc, "DECLARATION");
            docxParagraph(doc, DECLARATION);

            doc.write(out);
        }
    }

    private static void docxSectionHeader(XWPFDocument doc, String title) {
        XWPFParagraph p = doc.createParagraph();
        p.setBorderBottom(Borders.SINGLE);
        p.setSpacingBefore(160);
        p.setSpacingAfter(60);
        XWPFRun r = p.createRun();
        r.setBold(true);
        r.setFontSize(11);
        r.setText(title);
    }

    private static void docxParagraph(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingAfter(40);
        XWPFRun r = p.createRun();
        r.setFontSize(10);
        r.setText(text);
    }

    private static void docxBullets(XWPFDocument doc, java.util.List<String> items) {
        for (String item : items) {
            XWPFParagraph p = doc.createParagraph();
            p.setIndentationLeft(360);
            p.setSpacingAfter(20);
            XWPFRun r = p.createRun();
            r.setFontSize(10);
            r.setText("\u2022  " + item);
        }
    }

    private static void docxEntry(XWPFDocument doc, Entry e) {
        // Title (bold, left) + date (right, via a right tab stop)
        XWPFParagraph header = doc.createParagraph();
        header.setSpacingAfter(0);
        CTTabStop tab = header.getCTP().addNewPPr().addNewTabs().addNewTab();
        tab.setVal(STTabJc.RIGHT);
        tab.setPos(java.math.BigInteger.valueOf(9700));
        XWPFRun title = header.createRun();
        title.setBold(true);
        title.setFontSize(10);
        title.setText(e.title);
        if (e.date != null) {
            XWPFRun d = header.createRun();
            d.setFontSize(10);
            d.setText("\t" + e.date);
            d.setColor("444444");
        }
        if (e.subtitle != null) {
            XWPFParagraph sub = doc.createParagraph();
            sub.setSpacingAfter(20);
            XWPFRun s = sub.createRun();
            s.setItalic(true);
            s.setFontSize(10);
            s.setColor("444444");
            s.setText(e.subtitle);
        }
        if (!e.bullets.isEmpty()) docxBullets(doc, e.bullets);
    }
}
