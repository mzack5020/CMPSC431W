(function() {
    'use strict';
    angular
        .module('eLancerApp')
        .factory('Categories', Categories);

    Categories.$inject = ['$resource'];

    function Categories ($resource) {
        var resourceUrl =  'api/categories/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();

(function() {
    'use strict';
    angular
        .module('eLancerApp')
        .factory('CategoriesAll', CategoriesAll);

    CategoriesAll.$inject = ['$resource'];

    function CategoriesAll ($resource) {
        var resourceUrl =  'api/categoriesAll';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
