/**
 *
 */

angular.module('schedule', ['ui.bootstrap']);

angular.module('schedule')
    .factory('ScheduleService', ['$http', '$filter', '$log', function($http, $filter, $log){
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");
        var csrfValue = $("meta[name='_csrf']").attr("content");
        var csrf = {};
        csrf[csrfHeader] = csrfValue;

        return {
            getEmployees: function(shopId) {
                var path = '/api/shop/' + shopId + '/employees';
                return $http.get(path).then(function(response){
                    return response.data;
                }, function(response){
                    $log.error('Unable to load employees data. Reason: ' + response.statusText);
                    return response;
                });
            },

            getSchedule: function(shopId, date) {
                var path = '/api/schedule/' + shopId + '/' + $filter('date')(date, 'yyyy-MM-dd');
                return $http.get(path).then(function(response){
                    return response.data;
                }, function(response){
                    $log.error('Unable to load schedule data. Reason: ' + response.statusText);
                    return response;
                });
            },

            saveSchedule: function(schedule) {
                var path = '/api/schedule';
                return $http.post(path, schedule, {
                    headers: csrf
                }).then(function(response){
                    $log.info('Schedule successfully saved.');
                    return response.data;
                }, function(response){
                    $log.error('Unable to save schedule. Reason: ' + response.statusText);
                    $log.error(angular.toJson(response, true));
                    return response;
                });
            },

            getAvailableEmployeesKeys: function(employees, items) {
                var activeEmployeesKeys = Object.keys(employees).filter(function(empKey){
                    return employees[empKey].active;
                });

                var grouped = {};
                activeEmployeesKeys.forEach(function(empKey){
                    var dep = employees[empKey].position.department;
                    if(!(items[dep] && items[dep][empKey])) {
                        if(!grouped[dep])
                            grouped[dep] = [];
                        grouped[dep].push(empKey);
                    }
                });

                return grouped;
            }
        };
    }])
    .controller('ScheduleController', ['$scope', '$q', 'ScheduleService', function($scope, $q, ScheduleService){

        $scope.isVisible = false;
        $scope.allEmployees = {};
        $scope.schedule = {};
        $scope.employeesToAdd = {};
        $scope.date = Date.now();
        $scope.dateOptions = {
            datepickerMode: 'day',
            minMode: 'day',
            maxMode: 'day',
            showWeeks: false,
            startingDay: 1
        };

        $scope.loadShopData = function(event) {
            $scope.isVisible = false;
            $scope.date = Date.now();
            var target = $(event.target);
            $scope.shopId = target.data('shopId');
            $scope.shopName = target.text();
            var employeesPromise = ScheduleService.getEmployees($scope.shopId)
                .then(function(data){
                    $scope.allEmployees = data;
                });
            var schedulePromise = ScheduleService.getSchedule($scope.shopId, $scope.date)
                .then(function(data){
                    $scope.schedule = data;
                });

            $q.all([employeesPromise, schedulePromise])
                .then(function(){
                    $scope.employeesToAdd =
                        ScheduleService.getAvailableEmployeesKeys($scope.allEmployees, $scope.schedule.items);
                    $scope.isVisible = true;
                });
        };

        $scope.loadSchedule = function(shopId, date) {
            ScheduleService.getSchedule(shopId, date)
                .then(function(data){
                    $scope.schedule = data;
                    $scope.employeesToAdd =
                        ScheduleService.getAvailableEmployeesKeys($scope.allEmployees, data.items);
                });
        };

        $scope.clearItems = function() {
            if($scope.schedule) $scope.schedule.items = {};
        };

        $scope.saveSchedule = function() {
            ScheduleService.saveSchedule($scope.schedule)
                .then(function(version){
                    $scope.schedule.version = version;
                }, function(data){

                });
        };

    }]);