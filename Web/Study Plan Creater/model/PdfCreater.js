var pdfFiller   = require('pdffiller');
var sourcePDFPath = "./public/userDocument/studyplan.pdf";


function createPdf(destFilePath, username, firstName, lastName, sId, Email, Dept, Major, Concentration, Degree, gYear, gTerm, Opt, courseList){ 
    var courseYear = [];
    var courseTerm = [];
    var courseNumber = [];
    var courseName = [];
    var courseCredit = [];
    
    var courseYear2 = [];
    var courseTerm2 = [];
    var courseNumber2 = [];
    var courseName2 = [];
    var courseCredit2 = [];
    
    var courseYear3 = [];
    var courseTerm3 = [];
    var courseNumber3 = [];
    var courseName3 = [];
    var courseCredit3 = [];
    
    for(var i = 0; i < courseList.length; i++){
        var array = courseList[i].split(",");
         if(array[4] == "CoreCourse"){
            courseYear.push(array[0]);
            courseTerm.push(array[1]);
            courseNumber.push(array[2]);
            courseName.push(array[3]);
            courseCredit.push("3");
        }
        if(array[4] == "Electives"){
            courseYear2.push(array[0]);
            courseTerm2.push(array[1]);
            courseNumber2.push(array[2]);
            courseName2.push(array[3]);
            courseCredit2.push("3");
        }
        if(array[4] == "NonCredit"){
            courseYear3.push(array[0]);
            courseTerm3.push(array[1]);
            courseNumber3.push(array[2]);
            courseName3.push(array[3]);
            courseCredit3.push("0");
        }       
    }
    
    for(var i = courseYear.length; i < 12; i++){
        courseYear.push(" ");
        courseTerm.push(" ");
        courseNumber.push(" ");
        courseName.push(" ");
        courseCredit.push(" ");
    }
    
    for(var i = courseYear2.length; i < 5; i++){
        courseYear2.push(" ");
        courseTerm2.push(" ");
        courseNumber2.push(" ");
        courseName2.push(" ");
        courseCredit2.push(" ");
    }
    
    for(var i = courseYear3.length; i < 2; i++){
        courseYear3.push(" ");
        courseTerm3.push(" ");
        courseNumber3.push(" ");
        courseName3.push(" ");
        courseCredit3.push(" ");
    }
    
    var myOpt = "";
    var myGradTerm = "";
    var myDegree = "";

    if(Degree == "MA"){
        myDegree = "1";
    }
    if(Degree == "ME"){
        myDegree = "2";
    }
    if(Degree == "MS"){
        myDegree = "3";
    }
    if(Degree == "MPh"){
        myDegree = "4";
    }
    if(Degree == "MTM"){
        myDegree = "5";
    }
    if(Degree == "MBA"){
        myDegree = "6";
    }
    if(Degree == "EMBA"){
        myDegree = "7";
    }
    
    if(Opt == "Yes"){
        myOpt = "Choice1";
    }
    if(Opt == "No"){
        myOpt = "Choice2";
    }
    
    if(gTerm == "Spring"){
        myGradTerm = "Choice1";
    }
    if(gTerm == "Summer"){
        myGradTerm = "Choice2";
    }
    if(gTerm == "Fall"){
        myGradTerm = "Choice3";
    }
    
    
    var data = {
        "First Name" : firstName,
        "Last Name" : lastName,
        "Department" : Dept,
        "Major" : Major,
        "Student Identification No" : sId,
        "Concentration" : Concentration,
        "Exact Name of Degree Being Pursued" : Degree,
        
        "Group1" : "Choice1",
        "Group2" : gTerm,
        "Group3" : myOpt,
        "Group4" : myDegree,
        "Graduation Year" : gYear,
        
        
        
        "YearRow1" : courseYear[0],
        "TermRow1" : courseTerm[0],
        "Course NumberRow1" : courseNumber[0],
        "Course NameRow1" : courseName[0],
        "CreditsRow1" : courseCredit[0],
        
        "YearRow2" : courseYear[1],
        "TermRow2" : courseTerm[1],
        "Course NumberRow2" : courseNumber[1],
        "Course NameRow2" : courseName[1],
        "CreditsRow2" : courseCredit[1],
        
        "YearRow3" : courseYear[2],
        "TermRow3" : courseTerm[2],
        "Course NumberRow3" : courseNumber[2],
        "Course NameRow3" : courseName[2],
        "CreditsRow3" : courseCredit[2],
        
        "YearRow4" : courseYear[3],
        "TermRow4" : courseTerm[3],
        "Course NumberRow4" : courseNumber[3],
        "Course NameRow4" : courseName[3],
        "CreditsRow4" : courseCredit[3],
        
        "YearRow5" : courseYear[4],
        "TermRow5" : courseTerm[4],
        "Course NumberRow5" : courseNumber[4],
        "Course NameRow5" : courseName[4],
        "CreditsRow5" : courseCredit[4],
        
        "YearRow6" : courseYear[5],
        "TermRow6" : courseTerm[5],
        "Course NumberRow6" : courseNumber[5],
        "Course NameRow6" : courseName[5],
        "CreditsRow6" : courseCredit[5],
        
        "YearRow7" : courseYear[6],
        "TermRow7" : courseTerm[6],
        "Course NumberRow7" : courseNumber[6],
        "Course NameRow7" : courseName[6],
        "CreditsRow7" : courseCredit[6],
        
        "YearRow8" : courseYear[7],
        "TermRow8" : courseTerm[7],
        "Course NumberRow8" : courseNumber[7],
        "Course NameRow8" : courseName[7],
        "CreditsRow8" : courseCredit[7],
        
        "YearRow9" : courseYear[8],
        "TermRow9" : courseTerm[8],
        "Course NumberRow9" : courseNumber[8],
        "Course NameRow9" : courseName[8],
        "CreditsRow9" : courseCredit[8],
        
        "YearRow10" : courseYear[9],
        "TermRow10" : courseTerm[9],
        "Course NumberRow10" : courseNumber[9],
        "Course NameRow10" : courseName[9],
        "CreditsRow10" : courseCredit[9],
        
        "YearRow11" : courseYear[10],
        "TermRow11" : courseTerm[10],
        "Course NumberRow11" : courseNumber[10],
        "Course NameRow11" : courseName[10],
        "CreditsRow11" : courseCredit[10],
        
        "YearRow12" : courseYear[11],
        "TermRow12" : courseTerm[11],
        "Course NumberRow12" : courseNumber[11],
        "Course NameRow12" : courseName[11],
        "CreditsRow12" : courseCredit[11],
        
        
        
        "YearRow1_2" : courseYear2[0],
        "TermRow1_2" : courseTerm2[0],
        "Course NumberRow1_2" : courseNumber2[0],
        "Course NameRow1_2" : courseName2[0],
        "CreditsRow1_2" : courseCredit2[0],
        
        "YearRow2_2" : courseYear2[1],
        "TermRow2_2" : courseTerm2[1],
        "Course NumberRow2_2" : courseNumber2[1],
        "Course NameRow2_2" : courseName2[1],
        "CreditsRow2_2" : courseCredit2[1],
        
        "YearRow3_2" : courseYear2[2],
        "TermRow3_2" : courseTerm2[2],
        "Course NumberRow3_2" : courseNumber2[2],
        "Course NameRow3_2" : courseName2[2],
        "CreditsRow3_2" : courseCredit2[2],
        
        "YearRow4_2" : courseYear2[3],
        "TermRow4_2" : courseTerm2[3],
        "Course NumberRow4_2" : courseNumber2[3],
        "Course NameRow4_2" : courseName2[3],
        "CreditsRow4_2" : courseCredit2[3],
        
        "YearRow5_2" : courseYear2[4],
        "TermRow5_2" : courseTerm2[4],
        "Course NumberRow5_2" : courseNumber2[4],
        "Course NameRow5_2" : courseName2[4],
        "CreditsRow5_2" : courseCredit2[4],
        
        
        
        "YearRow1_3" : courseYear3[0],
        "TermRow1_3" : courseTerm3[0],
        "Course NumberRow1_3" : courseNumber3[0],
        "Course NameRow1_3" : courseName3[0],
        "CreditsRow1_3" : courseCredit3[0],
        
        "YearRow2_3" : courseYear3[1],
        "TermRow2_3" : courseTerm3[1],
        "Course NumberRow2_3" : courseNumber3[1],
        "Course NameRow2_3" : courseName3[1],
        "CreditsRow2_3" : courseCredit3[1]
    };

    pdfFiller.fillForm(sourcePDFPath, destFilePath, data, function(err) {
        if (err) throw err;
        console.log("In callback (we're done).");
    });
}  

exports.createPdf = createPdf;




