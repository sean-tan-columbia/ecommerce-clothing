var app = angular.module("closet-front", ['ngRoute'])

.config(function($routeProvider) {
    $routeProvider
        .when('/display', {
            templateUrl: 'display.html',
            controller: 'itemDetailReqController'
        })
        .when('/display/:itemId', {
            templateUrl: 'display.html',
            controller: 'itemDetailReqController'
        })
        .when('/', {
            templateUrl: 'home.html',
            controller: 'itemReqController'
        })
        .when('/admin', {
            templateUrl: 'admin.html',
            controller: 'newItemController'
        })
        .when('/update', {
            templateUrl: 'update.html',
            controller: 'newImageController'
        })
        .when('/update/:itemId', {
            templateUrl: 'update.html',
            controller: 'imageUploadController'
        })
        .otherwise({
            redirectTo: '/'
        });
})

.controller('itemListController', function($scope) {
    $scope.items = [
        {route: 'asset/img/closet-test-1.jpg', source: 'asset/img/closet-test-1.jpg', description: 'Add a Description of the image here.'},
        {route: 'asset/img/closet-test-2.jpg', source: 'asset/img/closet-test-2.jpg', description: 'Add a Description of the image here.'},
        {route: 'asset/img/closet-test-3.jpg', source: 'asset/img/closet-test-3.jpg', description: 'Add a Description of the image here.'},
        {route: 'asset/img/closet-test-4.jpg', source: 'asset/img/closet-test-4.jpg', description: 'Add a Description of the image here.'}
    ]
})

.controller('defaultItemListController', function($scope, $http) {
    $http.get("http://localhost:8080")
    .then(function(response) {
        $scope.items = response.data.items
    })
})

.controller('itemReqController', function($scope, $http) {
    $http.get("http://104.196.15.12:8080/show")
    .then(function(response) {
        $scope.items = response.data.items
    })
})

.controller('itemDetailReqController', function($scope, $http, $routeParams) {
    $http.get("http://104.196.15.12:8080/detail/" + $routeParams.itemId)
    .then(function(response) {
        $scope.items = response.data.images
    })
})

.controller('itemRequestController', function($scope) {
    var eventbus = new EventBus('http://localhost:8080/eventbus');
    eventbus.onopen = function() {
        eventbus.send('index.item.request', 'itemRequestController', function(error, response) {
            console.log(JSON.parse(response.body).items);
            $scope.$apply(function(){
                $scope.items = JSON.parse(response.body).items;
            });
        });
    }
})

.controller('itemDetailRequestController', function($scope, $routeParams) {
    var eventbus = new EventBus('http://localhost:8080/eventbus');
    eventbus.onopen = function() {
        eventbus.send('detail.item.request', $routeParams.itemId, function(error, response) {
            console.log(JSON.parse(response.body).items);
            $scope.$apply(function(){
                $scope.images = JSON.parse(response.body).images;
            });
        });
    }
})

.controller('newItemController', function($scope) {
    var eventbus = new EventBus('http://localhost:8080/eventbus');
    eventbus.onopen = function() {
        eventbus.send('index.item.request', 'newItemController', function(error, response) {
            console.log(JSON.parse(response.body).items);
            $scope.$apply(function(){
                $scope.items = JSON.parse(response.body).items;
            });
        });
    }
})

.controller('imageUploadController', function($scope, $routeParams, fileReader) {
    var eventbus = new EventBus('http://localhost:8080/eventbus');
    eventbus.onopen = function() {
        eventbus.send('detail.item.request', $routeParams.itemId, function(error, response) {
            console.log(JSON.parse(response.body).items);
            $scope.$apply(function(){
                $scope.images = JSON.parse(response.body).images;
            });
        });
    };
    $scope.getFile = function () {
        fileReader.readAsDataUrl($scope.file, $scope)
            .then(function(result) { $scope.imageSrc = result; });
    };
})

.directive("ngFileSelect",function(){
    return {
        link: function($scope,el){
            el.bind("change", function(e){
                $scope.file = (e.srcElement || e.target).files[0];
                $scope.getFile();
            })
        }
    }
})
;

(function (module) {

    var fileReader = function ($q, $log) {

        var onLoad = function(reader, deferred, scope) {
            return function () {
                scope.$apply(function () {
                    deferred.resolve(reader.result);
                });
            };
        };

        var onError = function (reader, deferred, scope) {
            return function () {
                scope.$apply(function () {
                    deferred.reject(reader.result);
                });
            };
        };

        var onProgress = function(reader, scope) {
            return function (event) {
                scope.$broadcast("fileProgress",
                    {
                        total: event.total,
                        loaded: event.loaded
                    });
            };
        };

        var getReader = function(deferred, scope) {
            var reader = new FileReader();
            reader.onload = onLoad(reader, deferred, scope);
            reader.onerror = onError(reader, deferred, scope);
            reader.onprogress = onProgress(reader, scope);
            return reader;
        };

        var readAsDataURL = function (file, scope) {
            var deferred = $q.defer();

            var reader = getReader(deferred, scope);
            reader.readAsDataURL(file);

            return deferred.promise;
        };

        return {
            readAsDataUrl: readAsDataURL
        };
    };

    module.factory("fileReader",
                   ["$q", "$log", fileReader]);

}(angular.module("closet-front")));
