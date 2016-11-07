(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('contractor', {
            parent: 'entity',
            url: '/contractor?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Contractors'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/contractor/contractors.html',
                    controller: 'ContractorController',
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
        .state('contractor-detail', {
            parent: 'entity',
            url: '/contractor/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Contractor'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/contractor/contractor-detail.html',
                    controller: 'ContractorDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Contractor', function($stateParams, Contractor) {
                    return Contractor.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contractor',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contractor-detail.edit', {
            parent: 'contractor-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/contractor/contractor-dialog.html',
                    controller: 'ContractorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Contractor', function(Contractor) {
                            return Contractor.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contractor.new', {
            parent: 'contractor',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/contractor/contractor-dialog.html',
                    controller: 'ContractorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                email: null,
                                phone: null,
                                rating: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('contractor', null, { reload: 'contractor' });
                }, function() {
                    $state.go('contractor');
                });
            }]
        })
        .state('contractor.edit', {
            parent: 'contractor',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/contractor/contractor-dialog.html',
                    controller: 'ContractorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Contractor', function(Contractor) {
                            return Contractor.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contractor', null, { reload: 'contractor' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contractor.delete', {
            parent: 'contractor',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/contractor/contractor-delete-dialog.html',
                    controller: 'ContractorDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Contractor', function(Contractor) {
                            return Contractor.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contractor', null, { reload: 'contractor' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
