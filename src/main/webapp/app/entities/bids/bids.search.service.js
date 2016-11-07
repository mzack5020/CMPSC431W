(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .factory('BidsSearch', BidsSearch);

    BidsSearch.$inject = ['$resource'];

    function BidsSearch($resource) {
        var resourceUrl =  'api/_search/bids/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
