(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('home', {
            parent: 'app',
            url: '/',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/home/home.html',
                    controller: 'HomeController',
                    controllerAs: 'vm'
                }
            }
        }).state('categorySortServices', {
            parent: 'app',
            url: '/sortByCategory',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Select a Category'
            },
            views: {
                'content@': {
                    templateUrl: 'app/home/select_category.html',
                    controller: 'SelectCategoryController',
                    controllerAs: 'vm'
                }
            }
        });
    }
})();
