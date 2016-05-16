angular.module('schedule', ['ui.bootstrap', 'ui-notification', 'time-table']);

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
            getAvailableEmployees: function(employees, items) {
                return employees.filter(function(emp) {
                    if(emp.active && items.find(function (item) {
                            return item.employee.id === emp.id;
                        })) return true;
                    return false;
                });
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
    .factory('ItemsAdapter', function () {
        return {
            adapt: function (items, groupBy) {
                return items.reduce(function (prev, curr) {
                    var dept = groupBy(curr);
                    var dIndex = prev.findIndex(function (elem) {
                        if (elem.departmentName === dept) return true;
                        return false;
                    });
                    if (dIndex < 0) {
                        dIndex = prev.push({departmentName: dept, employees: []}) - 1;
                    }
                    prev[dIndex].employees.push(curr);
                    return prev;
                }, []);
            },
            flat: function (grouped) {
                return grouped.reduce(function (prev, curr) {
                    return prev.concat(curr.employees);
                }, []);
            }
        }
    })
    .controller('ScheduleController', ['$scope', '$q', 'ScheduleService', 'RequestAnimationService', 'Notification',
        function($scope, $q, ScheduleService, RequestAnimationService, Notification){
        $scope.isVisible = false;
        $scope.employees = {};
        $scope.schedule = {};
        $scope.availableEmployees = {};
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
                    $scope.employees = data[0];
                    $scope.schedule = data[1];
                    $scope.availableEmployees = ItemsAdapter.adapt(
                        ScheduleService.getAvailableEmployees($scope.employees, $scope.schedule.items), function(emp) {
                            return emp.position.department;
                        });
                    $scope.scheduleLines = ItemsAdapter.adapt($scope.schedule.items, function(item) {
                        return item.employee.position.department;
                    });
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
                    $scope.availableEmployees = ItemsAdapter.adapt(
                        ScheduleService.getAvailableEmployees($scope.employees, $scope.schedule.items), function(emp) {
                            return emp.position.department;
                        });
                    $scope.scheduleLines = ItemsAdapter.adapt($scope.schedule.items, function(item) {
                        return item.employee.position.department;
                    });
                }, function(error){
                    Notification.error('<p>Error occurred while loading schedule data.</p>' +
                        '<p>Reason: '+ error.statusText +'.</p>');
                }).then(function(){
                    RequestAnimationService.hide();
            });
        };

        $scope.clearItems = function() {
            if($scope.schedule) $scope.scheduleLines = [];
        };

        $scope.saveSchedule = function() {
            RequestAnimationService.show();
            $scope.schedule.items = ItemsAdapter.flat($scope.scheduleLines);
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