(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .factory('RatingsSearch', RatingsSearch);

    RatingsSearch.$inject = ['$resource'];

    function RatingsSearch($resource) {
        var resourceUrl =  'api/_search/ratings/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
