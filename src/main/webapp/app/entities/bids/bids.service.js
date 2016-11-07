(function() {
    'use strict';
    angular
        .module('eLancerApp')
        .factory('Bids', Bids);

    Bids.$inject = ['$resource'];

    function Bids ($resource) {
        var resourceUrl =  'api/bids/:id';

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
