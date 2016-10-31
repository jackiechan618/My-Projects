(function(){
    'user strict';

    
    var app = angular.module('app',[]);
    
    app.controller("formPage1Ctrl",["$scope", "$http", function($scope, $http){
        $http.get('/formPage1/userInfo').success(function (data){
            if(data.length > 1) {
                var userInfo = data.split(":")
                $scope.firstName = userInfo[0];
                $scope.lastName = userInfo[1];
                $scope.sId = userInfo[2];
                $scope.Email = userInfo[3];
                $scope.Dept = userInfo[4];
                $scope.Major = userInfo[5];
                $scope.Concentration = userInfo[6];
                $scope.Degree = userInfo[7];
                $scope.gYear = userInfo[8];
                $scope.gTerm = userInfo[9];
                $scope.Opt = userInfo[10];
            }
        });
    }]);
    
    app.controller("courseCtrl",["$scope", "$http", "$location", function($scope, $http, $location){
        $scope.studyInfo = {
            year: "",
            term: "",
            major: "",
            course: ""
        };
             
        $scope.filterMajor="";
         
        $scope.majors = 
        [   
            "Computer Science",
            "Computer Engineering",
            "Electrical Engineering"
        ];
         
        $scope.courseYearList = [
            "2015",
            "2016",
            "2017",
            "2018",
            "2019",
            "2020",
            "2021"
        ];
        
        $scope.coursesInfos = [
            {"major": "Computer Science", "course": "CS-501,Introduction to JAVA Programming"},
            {"major": "Computer Science", "course": "CS-550,Comp Organization & Prog"},
            {"major": "Computer Science", "course": "CS-555,Agile Methods for Software Dev"},
            {"major": "Computer Science", "course": "CS-561,Database Management Systems I"},
            {"major": "Computer Science", "course": "CS-562,Database Management Systems II"},            
            {"major": "Computer Science", "course": "CS-570,Intro Program/Data Struct/Algor"},
            {"major": "Computer Science", "course": "CS-590,Algorithms"},
            {"major": "Computer Science", "course": "CS-600,Adv. Algorithm Dsgn & Implement"},
            
            {"major": "Computer Engineering", "course": "CPE-517,Digital & Comp Sys Architecture"},
            {"major": "Computer Engineering", "course": "CPE-521,Intro Autonomous Mobile Robots"},
            {"major": "Computer Engineering", "course": "CPE-545,Comm. Software & Middleware"},
            {"major": "Computer Engineering", "course": "CPE-555,Real-Time and Embedded Systems"},
            {"major": "Computer Engineering", "course": "CPE-556,Comp Princ Mobile & Embedded Sys"},
            {"major": "Computer Engineering", "course": "CPE-593,Applied Data Struct. & Algor."},
            
            {"major": "Electrical Engineering", "course": "EE-515,Photonics I"},
            {"major": "Electrical Engineering", "course": "EE-560,Introduction to Remote Sensing"},
            {"major": "Electrical Engineering", "course": "EE-605,Prob & Stochastic Proc I"},
            {"major": "Electrical Engineering", "course": "EE-609,Communication Theory I"},
            {"major": "Electrical Engineering", "course": "EE-613,Digital Signal Proc. for Comm."},
            {"major": "Electrical Engineering", "course": "EE-695,Applied Machine Learning"},
            {"major": "Electrical Engineering", "course": "EE-810,Special Topics in CPE"}
        ];
             
        $scope.userCourseList=[];
        $scope.userCourseList_send=[];
        $scope.userCourseList_display=[];
        
        $scope.selectCourseList = [];
        
        $scope.coreCourseNumber = 0;
        $scope.coreCourseCredit = 0;
        $scope.ElectivesNumber = 0;
        $scope.ElectivesCredit = 0;
        $scope.noCreditNumber = 0;
        $scope.totalCourseNumber = 0;
        $scope.totalCreditNumber = 0;
        
        $http.get('/formPage2/userInfo').success(function (data){
            if(data.length > 1) {
                var userInfo = data.split(":")
                $scope.firstName = userInfo[0];
                $scope.lastName = userInfo[1];
                $scope.sId = userInfo[2];
                $scope.Email = userInfo[3];
                $scope.Dept = userInfo[4];
                $scope.Major = userInfo[5];
                $scope.Concentration = userInfo[6];
                $scope.Degree = userInfo[7];
                $scope.gYear = "20" + userInfo[8];
                $scope.gTerm = userInfo[9];
                $scope.Opt = userInfo[10];
                
                var courseListStr = userInfo[11];
                var array = courseListStr.split(";");
                
                for(var i = 0; i < array.length; i++){
                    if(array[i].length > 0){
                        $scope.userCourseList_send.push(array[i]); 
                        var subArray = array[i].split(",");
                        
                        var displayCourseNode = {
                            "courseTime" : subArray[0] + "  " + subArray[1],
                            "courseName" : subArray[2] + " " + subArray[3] + ",  " + subArray[4]
                        };
                        
                        if(subArray[4] == "CoreCourse"){
                            $scope.coreCourseNumber = $scope.coreCourseNumber + 1;
                            $scope.totalCreditNumber = $scope.totalCreditNumber + 3;
                            $scope.coreCourseCredit = $scope.coreCourseCredit + 3;
                        }
                        if(subArray[4] == "Electives"){
                            $scope.ElectivesNumber = $scope.ElectivesNumber + 1;
                            $scope.totalCreditNumber = $scope.totalCreditNumber + 3;
                            $scope.ElectivesCredit = $scope.ElectivesCredit + 3;
                        }
                        if(subArray[4] == "NonCredit"){
                            $scope.noCreditNumber = $scope.noCreditNumber + 1;
                        }
                        
                        $scope.totalCourseNumber = $scope.totalCourseNumber + 1;
                        
                        $scope.userCourseList.push(subArray[2] + "," + subArray[3]); 
                        $scope.userCourseList_display.push(displayCourseNode);
                    }
                }
            }
        });
        
        $scope.courseYear = "";
        $scope.courseTerm = "";
        $scope.courseType = "";
                    
        $scope.addCourse = function(){            
            if($scope.studyInfo.course.length == 0){
                alert("You must choose a course first");
                return;
            }
            
            for(var i=0; i<$scope.userCourseList.length; i++){
                if($scope.userCourseList[i] == $scope.studyInfo.course){
                    alert("You have already added this course");
                    return;
                }
            }
            
//            if($scope.userCourseList.length == 12){
//                alert("You have already added 12 courses");
//                return;
//            }
            
            if($scope.courseYear.length == 0){
                alert("Please choose the year you want to take this course");
                return;
            }
            
            if($scope.courseTerm.length == 0){
                alert("Please choose the term you want to take this course");
                return;
            }
            
            if($scope.courseType.length == 0){
                alert("Please choose the type of the course");
                return;
            }
            
            if($scope.courseType == "CoreCourse" && $scope.coreCourseNumber >= 12){
                alert("You cannot choose more than 12 Core Courses");
                return;
            }
            if($scope.courseType == "Electives" && $scope.ElectivesNumber >= 5){
                alert("You cannot choose more than 5 Electives Courses");
                return;
            }
            if($scope.courseType == "NonCredit" && $scope.noCreditNumber >= 2){
                alert("You cannot choose more than 2 No Credit Courses");
                return;
            }
                
            
            var completeCourseInfo = $scope.courseYear + "," + $scope.courseTerm + "," + $scope.studyInfo.course + "," + $scope.courseType;
            
            var subArray = $scope.studyInfo.course.split(",");
            var displayCourseInfo = subArray[0] + " " + subArray[1];
            
            var displayCourseNode2 = {
                "courseTime" : $scope.courseYear + "  " + $scope.courseTerm,
                "courseName" : displayCourseInfo + ",  " + $scope.courseType,
                "courseType" : $scope.courseType
            };
            
            if($scope.courseType == "CoreCourse"){
                $scope.coreCourseNumber = $scope.coreCourseNumber + 1;
                $scope.totalCreditNumber = $scope.totalCreditNumber + 3;
                $scope.coreCourseCredit = $scope.coreCourseCredit + 3;
            }
            if($scope.courseType == "Electives"){
                $scope.ElectivesNumber = $scope.ElectivesNumber + 1;
                $scope.totalCreditNumber = $scope.totalCreditNumber + 3;
                $scope.ElectivesCredit = $scope.ElectivesCredit + 3;
            }
            if($scope.courseType == "NonCredit"){
                $scope.noCreditNumber = $scope.noCreditNumber + 1;
            }
            
            $scope.totalCourseNumber = $scope.totalCourseNumber + 1;
            
            $scope.userCourseList.push($scope.studyInfo.course);
            $scope.userCourseList_send.push(completeCourseInfo);
            
            $scope.userCourseList_display.push(displayCourseNode2);
            $scope.courseYear = "";
            $scope.courseTerm = "";
            $scope.courseType = "";
            
            for(var i=0; i<$scope.userCourseList.length; i++){
                if($scope.userCourseList[i].length == 0){
                    $scope.userCourseList.splice(i,1);
                    $scope.userCourseList_send.splice(i, 1);
                    $scope.userCourseList_display.splice(i, 1);
                }
            }           
        };
          
        
        $scope.deleteCourse = function(course){
            if(course.length == 0){
                return;
            }
       
            for(var i=0; i<$scope.userCourseList.length; i++){                                             
                if($scope.userCourseList_display[i] == course){                    
                    var subArray = $scope.userCourseList_send[i].split(",");
                                        
                    if(subArray[4] == "CoreCourse"){
                        $scope.coreCourseNumber = $scope.coreCourseNumber - 1;
                        $scope.totalCreditNumber = $scope.totalCreditNumber - 3;
                        $scope.coreCourseCredit = $scope.coreCourseCredit - 3;
                    }
                    if(subArray[4] == "Electives"){
                        $scope.ElectivesNumber = $scope.ElectivesNumber - 1;
                        $scope.totalCreditNumber = $scope.totalCreditNumber - 3;
                        $scope.ElectivesCredit = $scope.ElectivesCredit - 3;
                    }
                    if(subArray[4] == "NonCredit"){
                        $scope.noCreditNumber = $scope.noCreditNumber - 1;
                    }
                    
                    $scope.totalCourseNumber = $scope.totalCourseNumber - 1;
                                        
                    $scope.userCourseList_send.splice(i,1);
                    $scope.userCourseList.splice(i,1);
                    $scope.userCourseList_display.splice(i, 1);
                    
                    
                }
            }
        };
        
        
        $scope.majorSelect = function(){
            if($scope.filterMajor.length == 0){
                alert("You must choose a major first");
                return;
            }
            
            $scope.selectCourseList = [];
            
            for(var i = 0; i < $scope.coursesInfos.length; i++){
                if($scope.coursesInfos[i].major == $scope.filterMajor){
                    $scope.selectCourseList.push($scope.coursesInfos[i]);
                }
            }
        };
        
        $scope.courseListSubmit = function(){            
            $http.post('/formPage2', $scope.userCourseList_send).success(function(){
                console.log('Data update');
            })
            .error(function(data) {
                console.log('Data update failure');
            });    
        };            
    }]);
    
    app.controller("systemLogDisplayCtrl",["$scope", "$http", function($scope, $http){
        $scope.systemLogInfoList = [];
        
        $http.get('/SystemLogPage/systemLogInfo').success(function (data){
            $scope.systemLogInfoList = [];
            if(data.length > 1) {
                var systemLogInfoArray = data.split("$");
                
                for(var i = 0; i < systemLogInfoArray.length; i++){
                    if(systemLogInfoArray.length > 0){
                        $scope.systemLogInfoList.push(systemLogInfoArray[i]);
                    }
                }
            }
        });
    }]);
    
    app.controller("userInfoAdminCtrl",["$scope", "$http", function($scope, $http){
        $scope.userInfoList = [];
        
        $http.get('/userInfoAdmin/userInfo').success(function (data){
            $scope.userInfoList = [];
            if(data.length > 1) {
                var userInfoArray = data.split("$");
                
                for(var i = 0; i < userInfoArray.length; i++){
                    if(userInfoArray[i].length > 0){
                        var SingleUserInfo = userInfoArray[i].split(":");
                        var userInfoString =  '<Username: ' + SingleUserInfo[0] + ', Password: ' + SingleUserInfo[1] + '>';
                        $scope.userInfoList.push(userInfoString);
                    }
                }
            }
        });
    }]);
    
    app.controller("feedbackCtrl",["$scope", "$http", function($scope, $http){
        $scope.FeedbackInfoList = [];
        
        $http.get('/Feedback/feedbackInfo').success(function (data){
            $scope.FeedbackList = [];
            if(data.length > 1) {
                var FeedbackArray = data.split("$");
                
                for(var i = 0; i < FeedbackArray.length; i++){
                    if(FeedbackArray[i].length > 0){
                        var SingleFeedbackInfo = FeedbackArray[i].split(":");
                        var FeedbackInfoString = SingleFeedbackInfo[0] + ' <First Name: ' + SingleFeedbackInfo[1] + ', Last Name: ' + SingleFeedbackInfo[2] + ', Tel: ' + SingleFeedbackInfo[3] + '-' + SingleFeedbackInfo[4] + ', Email: ' + SingleFeedbackInfo[5] + ', Contact Method: ' + SingleFeedbackInfo[6] + ', Feedback: ' + SingleFeedbackInfo[7] + '>';

                        $scope.FeedbackInfoList.push(FeedbackInfoString);
                    }
                }
            }
        });
    }]);  
    
    app.controller("pdfPreviewCtrl",["$scope", "$http", function($scope, $http){      
        $scope.userPdfName = "userDocument/studyplan.pdf";
        
        $http.get('/pdfPreview/pdfInfo').success(function (data){
            if(data.length > 1) {
                $scope.userPdfName = "userDocument/" + data;
            } 
        });
    }]);    
    
})();
