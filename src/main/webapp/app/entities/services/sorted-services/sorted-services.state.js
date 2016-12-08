(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('sorted-services', {
            parent: 'entity',
            url: '/sortedServices/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Services (Sorted)'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/services/sorted-services/sorted-services.html',
                    controller: 'SortedServicesController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'SortedServices', function($stateParams, SortedServices) {
                    return SortedServices.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'services',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        });
    }

})();
