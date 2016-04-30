angular.module('TimeTable', []);

angular.module('TimeTable')
    .controller('TimeTableTestController', ['$scope', function ($scope) {
        $scope.scheduleItems = [
            [
                {
                    employeeId: 3,
                    firstName: 'FirstName3',
                    lastName: 'LastName3',
                    position: {
                        name: 'Manager',
                        department: 'Management'
                    },
                    startTime: 660,
                    duration: 480,
                    type: "WORK"
                },
                {
                    employeeId: 1,
                    firstName: 'FirstName1',
                    lastName: 'LastName1',
                    position: {
                        name: 'Administrator',
                        department: 'Management'
                    },
                    startTime: 720,
                    duration: 600,
                    type: "WORK"
                }
            ],
            [
                {
                    employeeId: 5,
                    firstName: 'FirstName5',
                    lastName: 'LastName5',
                    position: {
                        name: 'Saler',
                        department: 'Sales'
                    },
                    startTime: 600,
                    duration: 480,
                    type: "WORK"
                },
                {
                    employeeId: 4,
                    firstName: 'FirstName4',
                    lastName: 'LastName4',
                    position: {
                        name: 'Saler',
                        department: 'Sales'
                    },
                    startTime: 660,
                    duration: 540,
                    type: "WORK"
                },
                {
                    employeeId: 7,
                    firstName: 'FirstName7',
                    lastName: 'LastName7',
                    position: {
                        name: 'Saler',
                        department: 'Sales'
                    },
                    startTime: 720,
                    duration: 600,
                    type: "WORK"
                }
            ]
        ];
        $scope.scheduleItems[0].department = 'Management';
        $scope.scheduleItems[1].department = 'Sales';
    }]);

angular.module('TimeTable')
    .directive('timeTable', ['$document', '$window', function ($document, $window) {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: '../templates/timetable.html',
            scope: {
                schedule: '=',
                min: '@',
                max: '@',
                step: '@',
                workTypes: '@'
            },
            controller: function($scope) {



            },
            link: function($scope, $elem){
                var steps = {
                    "60": 1,
                    "30": 2,
                    "15": 4,
                    "10": 6
                };
                var timeAreaWidthRatio = 0.75;

                function calculateStep(decreaseStep) {
                    if(decreaseStep) {
                        var keys = Object.keys(steps);
                        var i = keys.indexOf('' + $scope.step) + 1;
                        $scope.step = keys[i] || 15;
                    }
                    $scope.partsPerHour = ($scope.step && steps[$scope.step]) || 4;
                    $scope.step = 60 / $scope.partsPerHour;
                }

                function calculateHours() {
                    var min  = $scope.min ? Math.floor($scope.min) : 8;
                    var max = $scope.max ? Math.ceil($scope.max) : 23;
                    if(min < 0) min = 8;
                    if(max > 24) max = 23;
                    if(max - min < 5) {
                        min = 8;
                        max = 23;
                    }
                    $scope.min = min;
                    $scope.max = max;
                    $scope.hoursCount = max - min;
                }

                function calculateWidthUnits() {
                    var areaWidth = $elem.width() * timeAreaWidthRatio;
                    var delta = 0;
                    do {
                        delta = Math.round(areaWidth / ($scope.hoursCount * $scope.partsPerHour));
                        if (delta < 5) calculateStep(true);
                    } while(delta < 5);
                    $scope.timeAreaWidth = delta * $scope.hoursCount * $scope.partsPerHour;
                    $scope.unitWidth = delta;
                }
                
                function drawTimeGrid() {
                    var timeTable = $('.time-table');
                    $('.time-table > .time-table-grid').remove();
                    var hourOffset = 0, hourWidth = $scope.unitWidth * $scope.partsPerHour;
                    while (hourOffset < $scope.timeAreaWidth){
                        hourOffset += hourWidth;
                        timeTable.append($('<div class="time-table-grid"></div>').css('right', hourOffset + 'px'));
                    }
                }

                function adaptToWidth() {
                    calculateWidthUnits();
                    drawTimeGrid();
                }

                function init() {
                    calculateStep();
                    calculateHours();
                    adaptToWidth();
                }
                
                init();

                var prevElementWidth = $elem.width();
                function resizeListener() {
                    var currentWidth = $elem.width();
                    if(prevElementWidth !== currentWidth) {
                        prevElementWidth = currentWidth;
                        $scope.$apply(adaptToWidth());
                    }
                }

                $window.addEventListener('resize', resizeListener);

                $elem.on('$destroy', function(){
                    $window.removeEventListener('resize', resizeListener);
                });

            }
        };
    }]);
