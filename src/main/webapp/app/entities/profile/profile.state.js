(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('profile', {
            parent: 'entity',
            url: '/profile',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'My Profile'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/profile/profile.html',
                    controller: 'ProfileController',
                    controllerAs: 'vm'
                }
            }
        })
        .state('profile-service-detail', {
            parent: 'profile',
            url: '/services/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Service'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/profile/profile-service-detail.html',
                    controller: 'ProfileDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Services', function($stateParams, Services) {
                    return Services.get({id : $stateParams.id}).$promise;
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
