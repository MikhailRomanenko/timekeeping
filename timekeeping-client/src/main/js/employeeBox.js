angular.module('EmployeeBox', []);

angular.module('EmployeeBox')
    .directive('employeeBox', function(){
        return {
            restrict: 'E',
            replace: true,
            scope: {
                departments: "=employees",
                select: "&"
            },
            templateUrl: '../templates/employee-box.html'
        };
    });
