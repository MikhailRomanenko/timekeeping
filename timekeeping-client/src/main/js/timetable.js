angular.module('TimeTable', []);

angular.module('TimeTable')
    .factory('throttle', function(){
        return function(func, wait, options) {
            var timeout, context, args, result;
            var previous = 0;
            if (!options) options = {};

            var later = function() {
                previous = options.leading === false ? 0 : Date.now();
                timeout = null;
                result = func.apply(context, args);
                if (!timeout) context = args = null;
            };

            var throttled = function() {
                var now = Date.now();
                if (!previous && options.leading === false) previous = now;
                var remaining = wait - (now - previous);
                context = this;
                args = arguments;
                if (remaining <= 0 || remaining > wait) {
                    if (timeout) {
                        clearTimeout(timeout);
                        timeout = null;
                    }
                    previous = now;
                    result = func.apply(context, args);
                    if (!timeout) context = args = null;
                } else if (!timeout && options.trailing !== false) {
                    timeout = setTimeout(later, remaining);
                }
                return result;
            };

            throttled.cancel = function() {
                clearTimeout(timeout);
                previous = 0;
                timeout = context = args = null;
            };

            return throttled;
        };
    })
    .filter('minutesToHours', function() {
        return function(minutes) {
            minutes = parseInt(minutes);
            if(isNaN(minutes)) {
                throw new Error('Input parameter is not a number');
            }
            var h = Math.floor(minutes / 60) + '';
            var m = minutes % 60 + '';
            h = h.length <= 1 ? '0' + h : h;
            m = m.length <= 1 ? '0' + m : m;
            return h + ':' + m;
        };
    })
    .filter('workingTime', ['$filter', function($filter){
        return function(start, duration) {
            start = parseInt(start);
            duration = parseInt(duration);
            if(isNaN(start) || isNaN(duration)) {
                throw new Error('Input parameter is not a number');
            }
            var from = $filter('minutesToHours')(start);
            var to = $filter('minutesToHours')(start + duration);
            var d = $filter('minutesToHours')(duration);
            return from + '-' + to + '(' + d + ')';
        };
    }]);

angular.module('TimeTable')
    .provider('timeTableDefaults', function() {
        var draggableConfig = {
                axis: 'X',
                containment: 'parent'
            },
            resizableConfig = {
                handles: 'e',
                containment: 'parent'
            },
            minDuration = 2,
            minMax = {
                min: 8,
                max: 23
            },
            step = 15,
            timeAreaWidthRatio = 0.75,
            steps = [10, 15, 30, 60],
            tTableTemplateURL = '../templates/timetable.html',
            tLineTemplateURL = '../templates/timeline.html';

        function acceptMinMax(newMinMax) {
            if(!newMinMax || !angular.isNumber(newMinMax.min) || !angular.isNumber(newMinMax.max)) return;
            var lMin = Math.floor(Math.abs(newMinMax.min));
            var lMax = Math.floor(Math.abs(newMinMax.max));
            if(lMin < 0 || lMin > 24 || lMax < 0 || lMax > 24 || lMin >= lMax) return;
            if(lMax - lMin > 4) {
                return {
                    min: lMin,
                    max: lMax
                };
            } else if(lMin + 4 <= 24) {
                return {
                    min: lMin,
                    max: lMin + 4
                };
            } else {
                return {
                    min: 20,
                    max: 24
                };
            }
        }
        return {
            setMinDuration: function(newDuration) {
                if(!Number.isInteger(newDuration)) return;
                newDuration = Math.abs(newDuration);
                if(newDuration >= 1 && newDuration <= 8)
                    minDuration = newDuration;
            },
            setMinMax: function(newMinMax) {
                var calculated = acceptMinMax(newMinMax);
                minMax = calculated ? calculated : {min: 8, max: 23};
            },
            setStep: function(newStep) {
                if(steps.find(function(element){
                        if(element === newStep)
                            return true;
                        return false;
                    })) step = newStep;
            },
            setTimeAreaWidthRatio: function(newRatio) {
                if(angular.isNumber(newRatio) && newRatio >= 0.3 && newRatio <= 0.9)
                    timeAreaWidthRatio = newRatio;
            },
            setTimeTableTemplateUrl: function(url) {
                tTableTemplateURL = url;
            },
            setTimeLineTemplateUrl: function(url) {
                tLineTemplateURL = url;
            },
            $get: function() {
                return {
                    normalizeMinMax: function(mm) {
                        var calculated = acceptMinMax(mm);
                        return calculated || minMax;
                    },
                    minDuration: minDuration,
                    draggableConfig: draggableConfig,
                    resizableConfig: resizableConfig,
                    minMax: minMax,
                    step: step,
                    steps: steps,
                    timeAreaRatio: timeAreaWidthRatio,
                    tableTemplate: tTableTemplateURL,
                    lineTemplate: tLineTemplateURL
                }
            }
        }
    });

angular.module('TimeTable')
    .directive('timeLine', ['throttle', 'timeTableDefaults', function(throttle, timeTableDefaults){
        return {
            restrict: 'E',
            replace: true,
            templateUrl: timeTableDefaults.lineTemplate,
            scope: false,
            require: '^^timeTable',
            controller: ['$scope', function($scope){
                $scope.setWorkType = function(type, line) {
                    line.type = type;
                };

                $scope.remove = function(line) {
                    var iDept = $scope.schedule.findIndex(function(elem){
                        if(line.position.department === elem.department) {
                            return true;
                        }
                        return false;
                    });
                    var iRemove = $scope.schedule[iDept].findIndex(function(elem){
                        if(elem.employeeId === line.employeeId) {
                            return true;
                        }
                        return false;
                    });
                    var removedEmployeeId = line.employeeId;
                    $scope.schedule[iDept].splice(iRemove, 1);
                    if($scope.schedule[iDept].length === 0) {
                        $scope.schedule.splice(iDept, 1);
                    }
                   $scope.onRemove({id: removedEmployeeId});
                };
            }],
            link: function(scope, element){
                var draggablePosition, draggableWidth, draggable;

                function recalculationHandler() {
                    element.find('.time-line-area').css('width', scope.timeAreaWidth + 'px');
                    element.find('.time-line-data').css('width', scope.dataAreaWidth + 'px');
                    draggable.draggable('option', 'grid', [ scope.unitWidth, 0 ]);
                    draggable.resizable('option', 'grid', [ scope.unitWidth, 0 ]);
                    draggable.resizable('option', 'minWidth', 60 * timeTableDefaults.minDuration / scope.step * scope.unitWidth);
                    draggableWidth = scope.schLine.duration / scope.step * scope.unitWidth;
                    draggablePosition = (scope.schLine.startTime - scope.minMax.min * 60) / scope.step * scope.unitWidth;
                    draggable.css('width', draggableWidth + 'px');
                    draggable.css('left', draggablePosition + 'px');
                }

                draggable = element.find('.time-line-area > div')
                    .resizable(timeTableDefaults.resizableConfig)
                    .draggable(timeTableDefaults.draggableConfig);

                draggable.on('drag', throttle(function(event, ui){
                    if(draggablePosition !== ui.position.left) {
                        draggablePosition = ui.position.left;
                        scope.schLine.startTime = (draggablePosition / scope.unitWidth) * scope.step +
                            scope.minMax.min * 60;
                        scope.$apply();
                    }
                }, 250));

                draggable.on('resize', throttle(function(event, ui){
                    draggableWidth = ui.size.width;
                    scope.schLine.duration = (draggableWidth / scope.unitWidth) * scope.step;
                    scope.$apply();
                }, 250));

                var unregisterRecalculation = scope.$on('unitsRecalculated', recalculationHandler);
                scope.$on('$destroy', function() {
                    unregisterRecalculation();
                });

                element.on('$destroy', function(){
                    draggable.off();
                });

                recalculationHandler();
            }
        };
    }]);

angular.module('TimeTable')
    .directive('timeTable', ['timeTableDefaults', '$window', function (timeTableDefaults, $window) {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: timeTableDefaults.tableTemplate,
            scope: {
                schedule: '=',
                minMax: '&',
                step: '&',
                workTypes: '&',
                onRemove: '&remove'
            },
            controller: ['$scope', function($scope) {
                $scope.visible = function() {
                    return $scope.schedule && $scope.schedule.length;
                };
            }],
            link: function(scope, elem){
                var steps = timeTableDefaults.steps,
                    partsPerHour, prevElementWidth = elem.width();
                scope.minMax = scope.minMax();
                scope.step = scope.step();
                scope.workTypes = scope.workTypes() || [];

                function calculateStep(increaseStep) {
                    if(increaseStep) {
                        var i = steps.indexOf(scope.step) + 1;
                        scope.step = steps[i] || steps[steps.length - 1];
                    }
                    if(!scope.step || !steps.find(function(element) {
                            if(element === scope.step) return true;
                            return false;
                        })) scope.step = timeTableDefaults.step;
                    partsPerHour = 60 / scope.step;
                }

                function calculateHours() {
                    scope.minMax = timeTableDefaults.normalizeMinMax(scope.minMax);
                }

                function calculateWidthUnits() {
                    var areaWidth = elem.width() * timeTableDefaults.timeAreaRatio;
                    var delta = 0, hoursCount = scope.minMax.max - scope.minMax.min;
                    do {
                        delta = Math.round(areaWidth / (hoursCount * partsPerHour));
                        if (delta < 5) calculateStep(true);
                    } while(delta < 5);
                    scope.timeAreaWidth = delta * hoursCount * partsPerHour;
                    scope.dataAreaWidth = elem.width() - scope.timeAreaWidth;
                    scope.unitWidth = delta;
                }

                function drawTimeGrid() {
                    var timeTable = $('.time-table');
                    $('.time-table > .time-table-grid').remove();
                    var hourOffset = 0, hourWidth = scope.unitWidth * partsPerHour;
                    while (hourOffset < scope.timeAreaWidth){
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

                function resizeListener() {
                    var currentWidth = elem.width();
                    if(prevElementWidth !== currentWidth) {
                        prevElementWidth = currentWidth;
                        scope.$apply(adaptToWidth());
                        scope.$broadcast('unitsRecalculated');
                    }
                }

                $window.addEventListener('resize', resizeListener);

                elem.on('$destroy', function(){
                    $window.removeEventListener('resize', resizeListener);
                });

                init();
            }
        };
    }]);
