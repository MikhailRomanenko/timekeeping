angular.module('employee-box', []);

angular.module('employee-box')
    .directive('employeeBox', function(){
        return {
            restrict: 'E',
            replace: true,
            scope: {
                departments: "=employees",
                onSelect: "&select"
            },
            controller: ['$scope', function($scope){
                 $scope.select = function(department, employee) {
                     var iDept = $scope.departments.findIndex(function(elem){
                         if(department === elem.departmentName) return true;
                         return false;
                     });
                     var iRemove = $scope.departments[iDept].employees.findIndex(function(elem){
                         if(elem.id === employee.id) return true;
                         return false;
                     });
                     $scope.departments[iDept].employees.splice(iRemove, 1);
                     if($scope.departments[iDept].employees.length === 0) 
                         $scope.departments.splice(iDept, 1);
                     if(angular.isFunction($scope.onSelect))
                         $scope.onSelect({employee: employee});
                 }
            }],
            templateUrl: '../templates/employee-box.html'
        };
    });
