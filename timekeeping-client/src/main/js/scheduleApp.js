angular.module('schedule', ['ui.bootstrap', 'ui-notification', 'TimeTable']);

angular.module('schedule')
    .config(function(NotificationProvider){
        NotificationProvider.setOptions({
            startTop: 70,
            startRight: 0,
            verticalSpacing: 20,
            horizontalSpacing: 20,
            positionX: 'center',
            positionY: 'top'
        });
    })
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
                }, function(error){
                    $log.error('Unable to load employees data. Reason: ' + error.statusText);
                    throw error;
                });
            },
            getSchedule: function(shopId, date) {
                var path = '/api/schedule/' + shopId + '/' + $filter('date')(date, 'yyyy-MM-dd');
                return $http.get(path).then(function(response){
                    return response.data;
                }, function(error){
                    $log.error('Unable to load schedule data. Reason: ' + error.statusText);
                    throw error;
                });
            },
            saveSchedule: function(schedule) {
                var path = '/api/schedule';
                return $http.post(path, schedule, {
                    headers: csrf
                }).then(function(response){
                    $log.info('Schedule successfully saved.');
                    return response.data;
                }, function(error){
                    $log.error('Unable to save schedule. Reason: ' + error.statusText);
                    throw error;
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
    .factory('RequestAnimationService', ['$uibModal', function($uibModal){
        var modal;
        var isOpened = false;
        return {
            show: function(){
                if(!isOpened) {
                    modal = $uibModal.open({
                        backdrop: 'static',
                        size: 'sm',
                        template: '<div class="sk-cube-grid">'+
                        '<div class="sk-cube sk-cube1"></div>'+
                        '<div class="sk-cube sk-cube2"></div>'+
                        '<div class="sk-cube sk-cube3"></div>'+
                        '<div class="sk-cube sk-cube4"></div>'+
                        '<div class="sk-cube sk-cube5"></div>'+
                        '<div class="sk-cube sk-cube6"></div>'+
                        '<div class="sk-cube sk-cube7"></div>'+
                        '<div class="sk-cube sk-cube8"></div>'+
                        '<div class="sk-cube sk-cube9"></div>'+
                        '</div>',
                        windowTopClass: 'transparent'
                    });
                    isOpened = true;
                }
            },
            hide: function(){
                if(modal){
                    modal.close();
                    isOpened = false;
                }
            }
        };
    }])
    .controller('ScheduleController', ['$scope', '$q', 'ScheduleService', 'RequestAnimationService', 'Notification',
        function($scope, $q, ScheduleService, RequestAnimationService, Notification){
        $scope.isVisible = false;
        $scope.allEmployees = {};
        $scope.schedule = {};
        $scope.employeesToAdd = {};
        $scope.date = Date.now();
        $scope.dateOptions = {
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
            RequestAnimationService.show();
            $q.all([ScheduleService.getEmployees($scope.shopId), ScheduleService.getSchedule($scope.shopId, $scope.date)])
                .then(function(data){
                    $scope.allEmployees = data[0];
                    $scope.schedule = data[1];
                    $scope.employeesToAdd =
                        ScheduleService.getAvailableEmployeesKeys($scope.allEmployees, $scope.schedule.items);
                    $scope.isVisible = true;
                }, function(error){
                    Notification.error('<p>Error occurred while loading data.</p>' +
                        '<p>Reason: '+ error.statusText +'.</p>');
                }).then(function(){
                    RequestAnimationService.hide();
            });
        };

        $scope.loadSchedule = function(shopId, date) {
            RequestAnimationService.show();
            ScheduleService.getSchedule(shopId, date)
                .then(function(schedule){
                    $scope.schedule = schedule;
                    $scope.employeesToAdd =
                        ScheduleService.getAvailableEmployeesKeys($scope.allEmployees, schedule.items);
                }, function(error){
                    Notification.error('<p>Error occurred while loading schedule data.</p>' +
                        '<p>Reason: '+ error.statusText +'.</p>');
                }).then(function(){
                    RequestAnimationService.hide();
            });
        };

        $scope.clearItems = function() {
            if($scope.schedule) $scope.schedule.items = {};
        };

        $scope.saveSchedule = function() {
            RequestAnimationService.show();
            ScheduleService.saveSchedule($scope.schedule)
                .then(function(version){
                    $scope.schedule.version = version;
                    Notification.success('<p>Schedule successfully saved. </p>');
                }, function(error){
                    Notification.error('<p>Schedule has not been saved. </p>' +
                        '<p>Reason: ' + error.statusText + '</p>');
                }).then(function(){
                    RequestAnimationService.hide();
            });
        };

    }]);