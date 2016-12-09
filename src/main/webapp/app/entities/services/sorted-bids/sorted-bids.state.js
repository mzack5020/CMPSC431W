(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('sorted-bids', {
            parent: 'entity',
            url: '/sorted-bids/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Bids'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/services/sorted-bids/sorted-bids.html',
                    controller: 'SortedBidsController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'SortedBids', function($stateParams, SortedBids) {
                    return SortedBids.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'Sorted-Bids',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        });
    }

})();
