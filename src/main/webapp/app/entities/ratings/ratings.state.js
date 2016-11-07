(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('ratings', {
            parent: 'entity',
            url: '/ratings?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Ratings'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/ratings/ratings.html',
                    controller: 'RatingsController',
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
        .state('ratings-detail', {
            parent: 'entity',
            url: '/ratings/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Ratings'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/ratings/ratings-detail.html',
                    controller: 'RatingsDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Ratings', function($stateParams, Ratings) {
                    return Ratings.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'ratings',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('ratings-detail.edit', {
            parent: 'ratings-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/ratings/ratings-dialog.html',
                    controller: 'RatingsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Ratings', function(Ratings) {
                            return Ratings.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('ratings.new', {
            parent: 'ratings',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/ratings/ratings-dialog.html',
                    controller: 'RatingsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                rating: null,
                                comments: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('ratings', null, { reload: 'ratings' });
                }, function() {
                    $state.go('ratings');
                });
            }]
        })
        .state('ratings.edit', {
            parent: 'ratings',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/ratings/ratings-dialog.html',
                    controller: 'RatingsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Ratings', function(Ratings) {
                            return Ratings.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('ratings', null, { reload: 'ratings' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('ratings.delete', {
            parent: 'ratings',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/ratings/ratings-delete-dialog.html',
                    controller: 'RatingsDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Ratings', function(Ratings) {
                            return Ratings.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('ratings', null, { reload: 'ratings' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
