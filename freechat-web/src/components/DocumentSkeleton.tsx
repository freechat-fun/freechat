import { forwardRef } from 'react';
import { Box, BoxProps, Skeleton, Stack } from '@mui/material';

type DocumentSkeletonProps = BoxProps & {
  lines?: number;
  loading?: boolean;
};

const DocumentSkeleton = forwardRef<HTMLDivElement, DocumentSkeletonProps>(
  (props, ref) => {
    const { lines = 1, loading = true, sx, ...others } = props;

    const titles = Array.from({ length: lines }, (_, index) => (
      <Stack key={`title-${index}`}>
        <Skeleton sx={{ height: '3rem' }} />
      </Stack>
    ));

    return (
      <Box
        ref={ref}
        sx={{
          display: loading ? 'grid' : 'none',
          gridTemplateColumns: '1fr 3fr',
          alignItems: 'stretch',
          gap: 3,
          pb: 4,
          pr: 4,
          ...sx,
        }}
        {...others}
      >
        <Stack>{titles}</Stack>
        <Stack sx={{ py: 1 }}>
          <Skeleton
            variant="rectangular"
            sx={{ borderRadius: '4px', flex: 1 }}
          />
        </Stack>
      </Box>
    );
  }
);

DocumentSkeleton.displayName = 'DocumentSkeleton';

export default DocumentSkeleton;
