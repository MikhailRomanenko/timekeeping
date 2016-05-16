angular.module('employee-box', []);

angular.module('employee-box')
    .directive('employeeBox', function(){
        return {
            restrict: 'E',
            replace: true,
            scope: {
                departments: "=employees",
                select: "&"
            },
            templateUrl: '/templates/employee-box.html'
        };
    });
