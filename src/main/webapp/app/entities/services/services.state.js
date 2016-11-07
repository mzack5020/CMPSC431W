(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('services', {
            parent: 'entity',
            url: '/services?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Services'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/services/services.html',
                    controller: 'ServicesController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
            }
        })
        .state('services-detail', {
            parent: 'entity',
            url: '/services/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Services'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/services/services-detail.html',
                    controller: 'ServicesDetailController',
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
        .state('services-detail.edit', {
            parent: 'services-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/services/services-dialog.html',
                    controller: 'ServicesDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Services', function(Services) {
                            return Services.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('services.new', {
            parent: 'services',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/services/services-dialog.html',
                    controller: 'ServicesDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                locationZip: null,
                                datePosted: null,
                                reportedCount: null,
                                photoPath: null,
                                expirationDate: null,
                                completed: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('services', null, { reload: 'services' });
                }, function() {
                    $state.go('services');
                });
            }]
        })
        .state('services.edit', {
            parent: 'services',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/services/services-dialog.html',
                    controller: 'ServicesDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Services', function(Services) {
                            return Services.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('services', null, { reload: 'services' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('services.delete', {
            parent: 'services',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/services/services-delete-dialog.html',
                    controller: 'ServicesDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Services', function(Services) {
                            return Services.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('services', null, { reload: 'services' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
