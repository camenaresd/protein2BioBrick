import java.awt.Desktop;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


/**
 * Create BioBrick sequence from Protein
 * 
 * As indicated by the title of this module, one or more protein sequences will be converted to biobricks optimized for expression in E. coli, without iGEM standard RE sites.
 * 
 * @author Devin Camenares, PhD
 *
 * @version 5-23-16
 * @since 5-23-16
 */
 
public final class protein2bioBrick extends Application {
 
    private Desktop desktop = Desktop.getDesktop();
 
	/**
         * This method was written by CC and modified by DJC, based upon code from StackOverflow user Kip.
         * It takes a string and writes it to a specified file, appending said file.
         * The purpose of this method is to save information and results as they are generated, reducing memory load.
         * 
         * @param content The string to be written
         * @param file The destination file for the string output
         */
	private void writeFile(String content, File file){
                try (FileWriter fw = new FileWriter(file, true)) {
                    content += System.lineSeparator();
                    fw.write(content);//

                } catch (IOException ex) 
                    {
		     Logger.getLogger(protein2bioBrick.class.getName()).log(Level.SEVERE, null, ex);
		    }
		         
            }        
        
	/**
         * This method was written by CC
         * 
         * @param file The desired file to be opened.
         */
        private void openFile(File file) {
            try {
		 desktop.open(file);
		} catch (IOException ex) {
		Logger.getLogger(
                 protein2bioBrick.class.getName()).log(
                 Level.SEVERE, null, ex
                 );
		 }
            }
    /**
     * A timestamp, used for generating unique file IDs
     */			 
    final long timeUnique = System.currentTimeMillis();
    
    /**
     * A directory name, created based upon timestamp.
     */
    public String dirName = Long.toString(timeUnique);
    

    /**
     * An array with restriction enzyme recognition sites for RFC12 standard
     */
    public String[] rfcS12 = {"EcoRI", "XbaI", "SpeI", "PstI", "NotI", "NheI", "PvuII", "XhoI", "AvrII", "SapI", "SapIA"};

    /**
     * An array with restriction enzyme recognition sites for RFC21 standard
     */    
    public String[] rfcS21 = {"EcoRI", "BglII", "BamHI", "XhoI"};       

    /**
     * An array with restriction enzyme recognition sites for RFC25 standard
     */    
    public String[] rfcS25 = {"EcoRI", "XbaI", "SpeI", "PstI", "NotI", "NgoMIV", "AgeI"};  

    /**
     * An array with restriction enzyme recognition sites for RFC25 standard
     */    
    public String[] goldenGateS = {"EcoRI", "XbaI", "SpeI", "PstI", "NotI", "BsaI", "BsmBI", "BsaI-RC", "BsmBI-RC"};
    
    /**
     * An array with restriction enzyme recognition sites for RFC25 standard
     */    
    public String[] rfcSall = {"EcoRI", "XbaI", "SpeI", "PstI", "NotI", "NgoMIV", "AgeI", "BglII", "BamHI", "XhoI", "NheI", "PvuII", "AvrII", "SapI", "SapIA"};  
      
    public String allTypes = "All standards";

    public String rfc10 = "RFC[10]";

    public String rfc12 = "RFC[12]";

    public String rfc21 = "RFC[21]";

    public String rfc23 = "RFC[23]";
    
    public String rfc25 = "RFC[25]";
    
    public String goldenGate = "GoldenGate-RFC10";
    
    public static String[] aminoArr = {"M", "G", "A", "V", "I", "L", "S", "T", "N", "Q", "D", "E", "H", "K", "R", "C", "F", "W", "Y", "P", "*", "X"};

    public static String[][] codonArr = new String[aminoArr.length][];

    /**
     * A counter to track the number of sequences processed
     */
    public int seqCount = 0;

    /**
     * A counter to help track int in general between functions
     */
    public int tempInt = 0;
    
    /**
     * Toggle to determine, based on user input, if the RFC prefix and suffixes should be added
     */
    public boolean addEnds = false;

    /**
     * Switch to check and make sure that codon choices were good, otherwise recode.
     */
    public static boolean goodChoice = false;
    
    @Override
    public void start(final Stage stage) {
        stage.setTitle("Creation of BioBrick DNA from Protein Sequences");

		// Create Grid pane, FileChooser, and Button
        final GridPane inputGridPane = new GridPane();		
        final FileChooser fileChooser = new FileChooser();
        final Button openButton = new Button("Open FASTA file");	
        final Button idButton = new Button("Generate New Job ID");	
 
 		// Text Fields and Labels. Some of this framework contributed by Christopher Camenares
		Label lbl1 = new Label("Job ID#:");
		lbl1.setMinHeight(50);
		lbl1.setMinWidth(250);                  

		Label lbl1A = new Label("Select Desired Assembly Standard");
		lbl1A.setMinHeight(50);
		lbl1A.setMinWidth(250); 
                
		Label lbl2 = new Label("Awaiting File Selection");
		lbl2.setMinHeight(50);
		lbl2.setMinWidth(100);
		
		TextField jobID = new TextField();
		jobID.setText(dirName);
		jobID.setMinHeight(50);
		jobID.setMinWidth(200);

		// Initialize ComboBoxes. Contributed by Christopher Camenares, with Modification
		final ComboBox<String> comboBox1;
		comboBox1 = new ComboBox<>();
		comboBox1.getItems().addAll(goldenGate, rfc10, rfc12, rfc21, rfc23, rfc25, allTypes);
		comboBox1.setMinWidth(100);
		comboBox1.setMinHeight(25);
		comboBox1.setValue(goldenGate);
	
		HBox comboBoxSection1;
		comboBoxSection1 = new HBox();
		comboBoxSection1.setSpacing(10);
		comboBoxSection1.getChildren().addAll(comboBox1);                

		CheckBox chck1;
		chck1 = new CheckBox("Add appropriate prefix and suffix");
		chck1.setSelected(true);                
                
        GridPane.setConstraints(openButton, 0, 7);
	GridPane.setConstraints(idButton, 0, 5);
	GridPane.setConstraints(lbl1, 0, 3);       
	GridPane.setConstraints(lbl2, 0, 6);
	GridPane.setConstraints(jobID, 0, 4);
	GridPane.setConstraints(lbl1A, 0, 0);  
        GridPane.setConstraints(comboBox1, 0, 1);
        GridPane.setConstraints(chck1, 0, 2);
        inputGridPane.setHgap(50);
        inputGridPane.setVgap(5);
        inputGridPane.getChildren().addAll(openButton, idButton, lbl1, lbl2, jobID, comboBox1, lbl1A, chck1);
        
        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));
 
        /**
         * Defines button action: generates new job ID
         */
        idButton.setOnAction(
			new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(final ActionEvent e) {
						final long timeButton = System.currentTimeMillis();                   
						jobID.setText(Long.toString(timeButton));
                    }
                }
            );
      
        openButton.setOnAction(
			new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    File file1 =
                        fileChooser.showOpenDialog(stage);
                    if (file1 != null) {
						
                        /**
                         * Beginning timestamp to track processing time
                         */
			final long timeStart = System.currentTimeMillis();                           
                        
			// Pull directory name from jobID text			
			dirName = jobID.getText();

                        // Assign Codons: In the future, this is done in response to user input
                        createCodonTable();
                        
                        /**
                         * The directory created by the program, wherein files are written
                         */
			File dir = new File(dirName);
			dir.mkdir();
			
			
                        /**
                         * String used to hold sequences / build sequences before writing
                         */
			String seqBody = new String();

                        /**
                         * String used to hold sequence header information
                         */
			String seqHeader = new String();                                             
                        
                        /**
                         * String used to hold pathnames
                         */
			String pathName = dirName + "\\protein2bioBrick_result.txt";
                        
                        /**
                         * File to which results will be written during run.
                         */
			File file3 = new File(pathName);

			pathName = dirName + "\\protein2bioBrick_report.txt";
                        
                        /**
                         * File to which runtime information is written upon program conclusion.
                         */
			File file4 = new File(pathName);

			pathName = dirName + "\\protein2bioBrick_backtrans.txt";
                        
                        /**
                         * File to which back translation check is written.
                         */
			File file5 = new File(pathName);                        
                        
                        /**
                         * Counter to determine number of iterations for escape fail safe
                         */
			int iterateFailSafe = 0;                      

                        /**
                         *  A block of code for determining what assembly standard to follow
                         */
                        String userChoice = comboBox1.getValue();

                        addEnds = chck1.isSelected();
                        
                           /**
                             * Value obtained from the user's choice of assembly standard
                             */
                            String assmStandard = comboBox1.getValue();

                            /**
                             * An array with enzyme recognition sites to the be removed. This is the default (RFC10), can be changed by user input. Also used for RFC23
                             */
                            String[] reSites = {"EcoRI", "XbaI", "SpeI", "PstI", "NotI"};
                            
                            /**
                             * Prefix sequence for assembly
                             */
                            String prefix = "";
                            
                            /**
                             * Prefix sequence for assembly
                             */
                            String suffix = "";
                            
                            switch (assmStandard)
                            {
                                case "GoldenGate-1":
                                    prefix = "GAATTCGCGGCCGCTTCTAG";
                                    suffix = "TACTAGTAGCGGCCGCTGCAG";
                                    break;
                                case "RFC[10]":
                                    prefix = "GAATTCGCGGCCGCTTCTAG";
                                    suffix = "TACTAGTAGCGGCCGCTGCAG";
                                    break;
                                case "RFC[12]":
                                    reSites = rfcS12;
                                    prefix = "GAATTCGCGGCCGCACTAGT";
                                    suffix = "GCTAGCGCGGCCGCTGCAG";                                    
                                    break;
                                case "RFC[21]":
                                    reSites = rfcS21;
                                    prefix = "GAATTCatgAGATCT";
                                    suffix = "GGATCCtaaCTCGAG";                                    
                                    break;
                                case "RFC[23]":
                                    prefix = "GAATTCGCGGCCGCTTCTAGA";
                                    suffix = "ACTAGTAGCGGCCGCTGCAG";                                   
                                    break;
                                case "RFC[25]":
                                    reSites = rfcS25;
                                    prefix = "GAATTCGCGGCCGCTTCTAGATGGCCGGC";
                                    suffix = "ACCGGTTAATACTAGTAGCGGCCGCTGCAG";                                    
                                    break;
                                case "All Types":
                                    reSites = rfcSall;                                    
                                    break;
                            }

                            String assmExtend = "none";

                            switch (assmExtend)
                            {
                                case "Gibson":
                                    prefix = "" + prefix;
                                    suffix = suffix + "";
                            }
                        
                        
                        // Convert Restriction Enzyme names to recognition pattern
                        
                        for (int i = 0; i < reSites.length; i++)
                         {
                          reSites[i] = rePattern(reSites[i]);
                         }
                            
                        /**
                         * String used to hold / build reporting / runtime information
                         */
			String repF = "Job ID# " + dirName + System.lineSeparator();
                        writeFile(repF, file4);                          
                        
                        /**
                         * A switch to help determine that body sequence be added.
                         */
                            boolean addBody = false;   
                            
                        // Begin Block of Code for Processing File  
                        try (BufferedReader br = new BufferedReader(new FileReader(file1))) {
                             
                             String line;
                             
                             while ((line = br.readLine()) != null) {
                             // process the line.                     
                             
                             if (addBody && line.contains(">"))
                             {
                                 
                                 processSeq(seqBody, seqHeader, reSites, prefix, suffix, file3, file4, file5);
                                 
                                 //Reset parameters
                                 seqBody = "";
                                 addBody = false;
                             }
                             
                             if (line.contains(">") && !addBody)
                             {

                                 seqHeader = line;
                                 addBody = true;   
                                 continue;
                             }
                             
                             if (addBody && !line.contains(">"));
                             {
                                 seqBody += line;
                                 continue;
                             }
                             
                             
                             }
                             // process the final collection
                                 
                                 processSeq(seqBody, seqHeader, reSites, prefix, suffix, file3, file4, file5);
                                 
                                 //Reset parameters
                                 seqBody = "";
                                 addBody = false;                            
                                 
                        } catch (FileNotFoundException ex) {
                          Logger.getLogger(protein2bioBrick.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                          Logger.getLogger(protein2bioBrick.class.getName()).log(Level.SEVERE, null, ex);
                        } 
						
						
                        /**
                         * Timestamp to determine the end of the program run and calculate processing time
                         */
			final long timeEnd = System.currentTimeMillis();
                        repF = "Source Filename: " + file1.getName() + System.lineSeparator();
                        repF += seqCount + " sequences retrieved" + System.lineSeparator();
                        repF += "Assembly Standard chosen: " + userChoice + System.lineSeparator();
                        if (addEnds)
                        {
                        repF += "Prefix and Suffix added" + System.lineSeparator();
                        }                        
			repF += Long.toString(timeEnd - timeStart) + " milliseconds of runtime";
						
			// Write the result to the file, report success
			writeFile(repF, file4);
			lbl2.setText("Files Saved!");
						
			openFile(dir);
				  
			}
			else
			{
			lbl2.setText("No files selected, please try again");
			}
                    }
                }
            );

 
        stage.setScene(new Scene(rootGroup));
        stage.show();
    }
 
    public static void main(String[] args) {
        Application.launch(args);
    }
 
    private static void configureFileChooser(
        final FileChooser fileChooser) {      
            fileChooser.setTitle("View Files");
            fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
            );                 
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plain Text", "*.txt"),
                new FileChooser.ExtensionFilter("FASTA", "*.fasta"),
                new FileChooser.ExtensionFilter("Rich Text Format", "*.rtf"),
			    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
    }
    
    /**
     * Reverse Translation function. Takes an amino acid and a codon usage preference number.
     * @param aminoAcid The amino acid to be reverse translated
     * @param codonUsage A number representing the fraction, of 0 to 1, from optimal to worst codon according to E coli codon usage
     * @return The nucleotide codon that encodes the amino acid
     */
    private static String revTrnsl(String aminoAcid, double codonUsage)
    {
        
        String theCodon = "nnn";
        aminoAcid = aminoAcid.toUpperCase();
        
        if (codonUsage > 1.0)
        {
         codonUsage = 1.0;
        }
        
        for (int i = 0; i < aminoArr.length; i++)
        {      
            
            if (aminoAcid.equals(aminoArr[i]))
            {
                int codonChoice = (int)(codonUsage * (codonArr[i].length)); 
                
                theCodon = codonArr[i][codonChoice];
                return theCodon;
            }

        }
        
        return theCodon; 
        
    }
    
    /**
     * A function to replace a codon with another synomous codon, in order to remove unwanted motifs
     * @param oldCodon The old nucleotide codon, to be replaced
     * @return The new codon
     */
    private static String newCodonRandom (String oldCodon, int attempts)
    {
        
        String aminoAcid = trnsL(oldCodon);

        String newCodon = oldCodon;
        
        if (!aminoAcid.equals("M") && !aminoAcid.equals("W"))
        {
        
        int failSafe = 0;    
            
        while (newCodon.equals(oldCodon) && failSafe < attempts)
        {
            newCodon = revTrnsl(aminoAcid, Math.random());
            
            if(!newCodon.equals(oldCodon))
            {
                goodChoice = true;
            }

            
            failSafe++;
        }
        
        }
        
        return newCodon;
    }
    
    /**
     * Translation function. Will take a nucleotide sequence and attempt to translate it into protein
     * @param nuc The nucleotide sequence. This should be at least three nucleotides long.
     * @return The protein / amino acid sequence
     */
    private static String trnsL (String nuc){
        int size = nuc.length();
        String prot = "";
        
        if (size + 1 % 3 == 0)
        {
            nuc += "aa";
        }

        if (size + 2 % 3 == 0)
        {
            nuc += "a";
        }
        
        if (size % 3 == 0)
        {
            for (int i = 0; i < size; i += 3)
            {
                String codon = nuc.substring(i, i + 3);  
                
                boolean matchFound = false;
                
                for (int j = 0; j < codonArr.length; j++)
                {
                    for (int k = 0; k < codonArr[j].length; k++)
                    {
                        if (codon.equals(codonArr[j][k]))
                        {
                            prot += aminoArr[j];
                            k = codonArr[j].length;
                            matchFound = true;
                        }
                    }
                    
                    if (matchFound)
                    {
                        j = codonArr.length;
                    }
                }
            }
            
        }
        
        return prot;
    }
    
    /**
     * Finds the positions within a motif that correspond to the codons in the larger open reading frame
     * @param motif The motif in question
     * @param pos The position of the motif in the larger sequence
     * @return The number, relative to the start of the motif, for codons in the open reading frame
     */
    private static int[] findFrame (String motif, int pos)
    {
        
        int resSize = motif.length() / 3;
        
        int[] res = new int[resSize];
        
        int a = pos;
        
        if (pos % 3 == 0)
        {
            for (int i = 0; i < resSize; i++)
            {
                res[i] = a;
                a += 3;
            }
        }
        else if ((pos + 1) % 3 == 0)
        {
            for (int i = 0; i < resSize; i++)
            {
                res[i] = a + 1;
                a += 3;
            }            
        }
        else if ((pos + 2) % 3 == 0)
        {
            for (int i = 0; i < resSize; i++)
            {
                res[i] = a + 2;
                a += 3;
            }                
        }
        
        return res;
    }
    
    /**
     * Counts the number of best, intermediate, or worst codons used, for giving an idea of the relative codon optimization.
     * @param seqBody
     * @return 
     */
    private static int[] assessCodons (String seqBody)
    {
        int[] res = {0, 0, 0, 0, 0, 0};
        
        for (int i = 0; i < seqBody.length(); i += 3)
        {
            
            String codon = seqBody.substring(i, i + 3);
            
             for (int j = 0; j < codonArr.length; j++)
                {
                    for (int k = 0; k < codonArr[j].length; k++)
                    {
                        if (codon.equals(codonArr[j][k]))
                        {
                            res[k]++;
                        }
                    } 
                }
        }
        
        return res;
    }
    
    /**
     * A method to hold values for a codon table. Eventually will take input to change table
     */
    private static void createCodonTable(){
              
       String[] metCodons = {"atg"};
       codonArr[0] = metCodons;
                        
       String[] glyCodons = {"ggc", "ggt", "ggg", "gga"};
       codonArr[1] = glyCodons;

       String[] alaCodons = {"gcg", "gcc", "gca", "gct"};
       codonArr[2] = alaCodons;                        
                        
       String[] valCodons = {"gtg", "gtt", "gtc", "gta"};
       codonArr[3] = valCodons;                           

       String[] ileCodons = {"att", "atc", "ata"};
       codonArr[4] = ileCodons; 
                        
       String[] leuCodons = {"ctg", "tta", "ttg", "ctt", "ctc", "cta"};
       codonArr[5] = leuCodons; 
                        
       String[] serCodons = {"agc", "tcg", "agt", "tcc", "tct", "tca"};
       codonArr[6] = serCodons; 
                        
       String[] thrCodons = {"acc", "acg", "act", "aca"};
       codonArr[7] = thrCodons; 
                        
       String[] asnCodons = {"aac", "aat"};
       codonArr[8] = asnCodons; 
                        
       String[] glnCodons = {"cag", "caa"};
       codonArr[9] = glnCodons; 
                        
       String[] aspCodons = {"gat", "gac"};
       codonArr[10] = aspCodons; 
                        
       String[] gluCodons = {"gaa", "gag"};
       codonArr[11] = gluCodons; 
                        
       String[] hisCodons = {"cat", "cac"};
       codonArr[12] = hisCodons; 
                        
       String[] lysCodons = {"aaa", "aag"};
       codonArr[13] = lysCodons; 
                        
       String[] argCodons = {"cgc", "cgt", "cgg", "cga", "aga", "agg"};
       codonArr[14] = argCodons; 
                        
       String[] cysCodons = {"tgc", "tgt"};
       codonArr[15] = cysCodons; 
                        
       String[] pheCodons = {"ttt", "ttc"};
       codonArr[16] = pheCodons; 
                        
       String[] trpCodons = {"tgg"};
       codonArr[17] = trpCodons; 
                        
       String[] tyrCodons = {"tat", "tac"};
       codonArr[18] = tyrCodons; 
                        
       String[] proCodons = {"ccg", "cca", "cct", "ccc"};
       codonArr[19] = proCodons; 
                        
       String[] stopCodons = {"taa", "tga", "tag"};
       codonArr[20] = stopCodons; 
       
       // Use Alanine in place of unknown amino acid.
       String[] unkCodons = {"gcg", "gcc", "gca", "gct"};
       codonArr[21] = unkCodons;
                        
    }
    
    /**
     * A method that stores restriction enzyme pattern information
     * @param re The name of the restriction enzyme
     * @return The sequence motif for the enzyme
     */
    private static String rePattern (String re)
    {
        String res = "";
        
        switch (re)
        {
            case "EcoRI":
                res = "gaattc";
                break;
            case "XbaI":
                res = "tctaga";
                break;
            case "SpeI":
                res = "actagt";
                break;
            case "PstI":
                res = "ctgcag";
                break;
            case "NotI":
                res = "gcggccgc";
                break;
            case "NheI":
                res = "gctagc";
                break;
            case "PvuII":
                res = "cagctg";
                break;
            case "XhoI":
                res = "ctcgag";
                break;
            case "AvrII":
                res = "cctagg";
                break;
            case "SapI":
                res = "gctcttc";
                break;                 
            case "SapIA":
                res = "gaagagc";
                break;
            case "BglII":
                res = "agatct";
                break;
            case "BamHI":
                res = "ggatcc";
                break;
            case "NgoMIV":
                res = "gccggc";
                break;
            case "AgeI":
                res = "accggt";
                break;                  
            case "BsaI":
                res = "ggtctc";
                break;         
            case "BsmBI":
                res = "cgtctc";
                break;
            case "BsaI-RC":
                res = "gagacc";
                break;         
            case "BsmBI-RC":
                res = "gagacg";
                break;                 
        }
        
        return res;
    }
    
    /**
     * A function that encapsulates the main processing routine. Stored as separate function since it is used twice.
     * @param seqBody The protein sequence
     * @param seqHeader The sequence header
     * @param reSites An array of restriction enzyme site patterns to avoid
     * @param prefix The RFC prefix
     * @param suffix The RFC suffix
     * @param file3 The file to which the resulting DNA sequence will be written
     * @param file4 The file to which the runtime information will be written
     * @param file5 The file to which the back-translation will be written
     */
    private void processSeq(String seqBody, String seqHeader, String[] reSites, String prefix, String suffix, File file3, File file4, File file5)
    {

                        writeFile(seqHeader, file3);

                        writeFile("Runtime Information for sequence " + seqCount, file4);                       
                        
                        /**
                         * A counter to escape an infinite or long looop
                         */
                        int iterateFailSafe = 0;
                                 
                        /**
                         * An array of amino acids from the protein sequence
                         */
                        String[] protSeq = seqBody.split("");

                        writeFile("Protein sequence is " + protSeq.length + " amino acids long", file4);                                              
                        
                        /**
                         * An array to hold the nucleotides / codons
                         */
                        String[] nucArr = new String[protSeq.length];
                                 
                        seqBody = "";
                                 
                        // Reverse translate
                        for (int i = 0; i < protSeq.length; i++)
                         {
                            nucArr[i] = revTrnsl(protSeq[i], 0.0);                                     
                            seqBody += nucArr[i];
                         }
                                 
                        // Check for repetitions
                        for (int a = 0; a < seqBody.length() - 9; a++)
                         {
                             /**
                              * The pattern to be used to check for reptitions
                              */
                            String seqPat = seqBody.substring(a, a + 8);
                            
                            /**
                             * The matching index of the pattern
                             */
                            int matIndex = seqBody.indexOf(seqPat, a + 1);

                            if (matIndex != -1)
                             {

 
                               int[] motifFrame = findFrame(seqPat, matIndex);
                                        
                               goodChoice = false;
                                        
                               for (int j = 0; j < motifFrame.length; j++)
                                {
                                    int codonPos = motifFrame[j];
                                            
                                    String upSeq = seqBody.substring(0, codonPos);
                                    String codon = seqBody.substring(codonPos, codonPos + 3);
                                            
                                    int trys = 100;
                                            
                                    if (goodChoice)
                                     {
                                        trys = 3;
                                     }
                                            
                                    codon = newCodonRandom(codon, trys);
                                    String downSeq = seqBody.substring(codonPos + 3, seqBody.length());
                                        
                                    seqBody = upSeq + codon + downSeq;
                                        
                                 }
                               
                                writeFile("Found reptitive motif " + seqPat + " at position " + matIndex +  ". Replaced with sequence " + seqBody.substring(matIndex, matIndex + 8), file4);
                                        

                             }

                                     
                         }
                                 
                        // Check for RE sites, fix if found
                        for (int i = 0; i < reSites.length; )
                         {
                            int checkRE = seqBody.indexOf(reSites[i]);
                                     
                            if(checkRE > -1 && iterateFailSafe < 100)
                             {    
                                iterateFailSafe++;
                                writeFile("Found restriction enzyme motif " + reSites[i] + " at position " + checkRE, file4);
                                goodChoice = false;
                                        
                                int[] motifFrame = findFrame(reSites[i], checkRE);
                                        
                                for (int j = 0; j < motifFrame.length; j++)
                                 {
                                    int codonPos = motifFrame[j];
                                    String upSeq = seqBody.substring(0, codonPos);
                                    String codon = seqBody.substring(codonPos, codonPos + 3);
                                            
                                    int trys = 100;
                                            
                                    if (goodChoice)
                                     {
                                        trys = 3;
                                     }
                                            
                                            
                                    codon = newCodonRandom(codon, trys);
                                    String downSeq = seqBody.substring(codonPos + 3, seqBody.length());
                                        
                                    seqBody = upSeq + codon + downSeq;
                                        
                                 }
                                        
                                i = 0;
                                        
                             }
                            else
                             {
                                writeFile("Found no instance of restriction enzyme motif" + reSites[i], file4);
                                goodChoice = true;
                                i++;
                                iterateFailSafe = 0;
                             }
                        }                              
                                 
                        //Write results
                        if(addEnds)
                         {
                            writeFile(prefix + seqBody + suffix + System.lineSeparator(), file3);                                     
                         }
                        else
                         {
                            writeFile(seqBody + System.lineSeparator(), file3);
                         }
                                 
                        //Back translation
                        
                        int[] codonRanks = assessCodons(seqBody);
                        
                        writeFile(codonRanks[0] + " instances of 1st ranked codons.", file4);                        
                        writeFile(codonRanks[1] + " instances of 2nd ranked codons.", file4);                        
                        writeFile(codonRanks[2] + " instances of 3rd ranked codons.", file4);                        
                        writeFile(codonRanks[3] + " instances of 4th ranked codons.", file4);                        
                        writeFile(codonRanks[4] + " instances of 5th ranked codons.", file4);                        
                        writeFile(codonRanks[5] + " instances of 6th ranked codons.", file4);                        
                        
                        writeFile(System.lineSeparator() + "*" + System.lineSeparator(), file4);                                                
                        
                        seqBody = trnsL(seqBody);
                        writeFile(seqHeader + System.lineSeparator(), file5);                                 
                        writeFile(seqBody + System.lineSeparator(), file5);
                                 
    }
}