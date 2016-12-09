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
        })
        .state('profile.newRating', {
            parent: 'profile-service-detail',
            url: '/newRating/{id}',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/ratings/ratings-dialog.html',
                    controller: 'ProfileNewRatingController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                customer: null,
                                contractor: null,
                                services: null,
                                rating: null,
                                comments: null,
                                id: null
                            };
                        },
                        serviceId: function () {
                            return {
                                id: $stateParams.id
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('profile-service-detail', null, { reload: 'profile-service-detail' });
                }, function() {
                    $state.go('profile-service-detail');
                });
            }]
        });
    }
})();
