(function() {
    'use strict';
    angular
        .module('eLancerApp')
        .factory('Contractor', Contractor);

    Contractor.$inject = ['$resource'];

    function Contractor ($resource) {
        var resourceUrl =  'api/contractors/:id';

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
