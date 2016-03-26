var app = angular.module("closet-front", ['ngRoute'])

//.config(function($routeProvider) {
//    $routeProvider
//        .when('/display', {
//            templateUrl: 'display.html',
//            controller: 'itemDetailRequestController'
//        })
//        .otherwise({
//            redirectTo: '/'
//        });
//})

.controller('itemListController', function($scope) {
    $scope.items = [
        {route: 'asset/img/closet-test-1.jpg', source: 'asset/img/closet-test-1.jpg', description: 'Add a Description of the image here.'},
        {route: 'asset/img/closet-test-2.jpg', source: 'asset/img/closet-test-2.jpg', description: 'Add a Description of the image here.'},
        {route: 'asset/img/closet-test-3.jpg', source: 'asset/img/closet-test-3.jpg', description: 'Add a Description of the image here.'},
        {route: 'asset/img/closet-test-4.jpg', source: 'asset/img/closet-test-4.jpg', description: 'Add a Description of the image here.'}
    ]
})

.controller('myCtrl', function($scope, $http) {
    $http.get("http://jsonplaceholder.typicode.com/posts/1")
    .then(function(response) {
        console.log(response.data)
    })
})

.controller('defaultItemListController', function($scope, $http) {
    $http.get("http://localhost:8080")
    .then(function(response) {
        $scope.items = response.data.items
    })
})

.controller('defaultItemRequestController', function($scope) {
    var eventbus = new EventBus('http://localhost:8080/eventbus');
    eventbus.onopen = function() {
        eventbus.send('index.item.request', 'I am handsome!', function(error, response) {
            console.log(JSON.parse(response.body).items);
            $scope.$apply(function(){
                $scope.items = JSON.parse(response.body).items;
            });
        });
    }
})

.controller('itemDetailRequestController', function($scope, $routeParams, $locationProvider) {
    var eventbus = new EventBus('http://localhost:8080/eventbus');
    console.log($routeParams.itemId)
    eventbus.onopen = function() {
        eventbus.send('detail.item.request', $routeParams.itemId, function(error, response) {
            console.log(JSON.parse(response.body).items);
            $scope.$apply(function(){
                $scope.images = JSON.parse(response.body).images;
            });
        });
    }
})
;