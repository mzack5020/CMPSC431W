(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('RatingsDetailController', RatingsDetailController);

    RatingsDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Ratings', 'Customer', 'Contractor', 'Services'];

    function RatingsDetailController($scope, $rootScope, $stateParams, previousState, entity, Ratings, Customer, Contractor, Services) {
        var vm = this;

        vm.ratings = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('eLancerApp:ratingsUpdate', function(event, result) {
            vm.ratings = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
