# CV Generator (Java)

A small Java/Maven application that produces a professional CV/resume as **both a PDF
and a DOCX** file, styled after a classic single-column resume (centered name/contact
header, underlined section titles, dated entries, and bullet points).

- **PDF** is rendered with [iText 7](https://itextpdf.com/).
- **DOCX** is rendered with [Apache POI](https://poi.apache.org/).

All the code is in a single class: `src/main/java/com/codealpha/cv/CVGenerator.java`.

## Run in IntelliJ IDEA

1. `File > Open` and select this `cv-generator` folder (IntelliJ detects the Maven
   project from `pom.xml` and downloads the dependencies automatically).
2. Open `CVGenerator.java`, then click the green ▶ gutter arrow next to `main`
   (or right-click the file → **Run 'CVGenerator.main()'**).

It writes `Edward_Abasi_Edgar_CV.pdf` and `Edward_Abasi_Edgar_CV.docx` into the
project directory.

## Run from the command line

```bash
cd cv-generator
mvn compile exec:java
```

## Customizing the content

The CV content lives in clearly labeled `static` fields near the top of
`CVGenerator.java` (name, contact, summary, work experience, education, references,
etc.). Edit those values (or the `Entry` lists) and re-run — no layout code needs to
change.

Requires JDK 17+ and Maven.
