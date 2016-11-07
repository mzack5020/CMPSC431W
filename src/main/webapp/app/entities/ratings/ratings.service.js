(function() {
    'use strict';
    angular
        .module('eLancerApp')
        .factory('Ratings', Ratings);

    Ratings.$inject = ['$resource'];

    function Ratings ($resource) {
        var resourceUrl =  'api/ratings/:id';

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
